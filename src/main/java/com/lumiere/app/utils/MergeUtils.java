package com.lumiere.app.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class MergeUtils {

    private MergeUtils() {}

    public static class Options {
        private boolean overwriteNulls = true;              // true: null mới sẽ ghi đè -> xóa giá trị cũ
        private boolean replaceCollections = true;          // true: set/list/map mới -> thay thế hẳn
        private Set<String> excludeProps = Set.of(          // bỏ qua các field hệ thống/id
            "id", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate", "version"
        );

        public Options overwriteNulls(boolean v) { this.overwriteNulls = v; return this; }
        public Options replaceCollections(boolean v) { this.replaceCollections = v; return this; }
        public Options excludeProps(Set<String> props) { this.excludeProps = props; return this; }
    }

    /**
     * Merge từ source -> target theo Options.
     * - Nếu overwriteNulls=true: giá trị null ở source sẽ set null vào target (xóa).
     * - Collections/Map: nếu replaceCollections=true thì thay toàn bộ; ngược lại bỏ qua (tránh merge sâu phức tạp).
     */
    public static <S, T> T merge(S source, T target, Options opts) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(target, "target");

        BeanWrapper src = new BeanWrapperImpl(source);
        BeanWrapper trg = new BeanWrapperImpl(target);

        for (PropertyDescriptor pd : src.getPropertyDescriptors()) {
            String name = pd.getName();

            // Bỏ qua prop đặc biệt hoặc không writable
            if ("class".equals(name) || opts.excludeProps.contains(name)) continue;
            if (!trg.isWritableProperty(name) || !src.isReadableProperty(name)) continue;

            Object newVal = src.getPropertyValue(name);
            Class<?> propType = pd.getPropertyType();

            // Collection/Map xử lý riêng
            if (Collection.class.isAssignableFrom(propType) || Map.class.isAssignableFrom(propType)) {
                if (!opts.replaceCollections) continue; // giữ nguyên
                trg.setPropertyValue(name, newVal);     // thay luôn
                continue;
            }

            // Giá trị thường
            if (newVal != null) {
                trg.setPropertyValue(name, newVal);
            } else if (opts.overwriteNulls) {
                // null mới -> xóa giá trị cũ
                trg.setPropertyValue(name, null);
            }
        }
        return target;
    }

    /** Tiện ích: copy non-null only (PATCH kiểu cũ) */
    public static <S, T> T copyNonNull(S source, T target, Set<String> excludeProps) {
        BeanWrapper src = new BeanWrapperImpl(source);
        BeanWrapper trg = new BeanWrapperImpl(target);

        for (PropertyDescriptor pd : src.getPropertyDescriptors()) {
            String name = pd.getName();
            if ("class".equals(name) || (excludeProps != null && excludeProps.contains(name))) continue;
            if (!trg.isWritableProperty(name) || !src.isReadableProperty(name)) continue;

            Object newVal = src.getPropertyValue(name);
            if (newVal != null) {
                trg.setPropertyValue(name, newVal);
            }
        }
        return target;
    }
}
