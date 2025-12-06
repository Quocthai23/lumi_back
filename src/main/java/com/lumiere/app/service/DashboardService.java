package com.lumiere.app.service;

import com.lumiere.app.service.dto.DashboardStatsDTO;
import com.lumiere.app.service.dto.MonthlyRevenueDTO;
import com.lumiere.app.service.dto.MonthlyCustomerDTO;
import com.lumiere.app.service.dto.TopProductDTO;
import com.lumiere.app.service.dto.RecentSaleDTO;
import java.util.List;

/**
 * Service Interface for Dashboard statistics.
 */
public interface DashboardService {
    /**
     * Lấy thống kê tổng quan cho dashboard.
     *
     * @return DashboardStatsDTO chứa các thống kê tổng quan
     */
    DashboardStatsDTO getDashboardStats();

    /**
     * Lấy doanh thu theo tháng trong năm hiện tại.
     *
     * @return Danh sách doanh thu theo tháng
     */
    List<MonthlyRevenueDTO> getRevenueByMonth();

    /**
     * Lấy số lượng khách hàng mới theo tháng trong năm hiện tại.
     *
     * @return Danh sách số lượng khách hàng mới theo tháng
     */
    List<MonthlyCustomerDTO> getNewCustomersByMonth();

    /**
     * Lấy top sản phẩm bán chạy nhất.
     *
     * @param limit số lượng sản phẩm cần lấy (mặc định 5)
     * @return Danh sách top sản phẩm bán chạy
     */
    List<TopProductDTO> getTopProducts(int limit);

    /**
     * Lấy danh sách đơn hàng gần đây.
     *
     * @param limit số lượng đơn hàng cần lấy (mặc định 5)
     * @return Danh sách đơn hàng gần đây
     */
    List<RecentSaleDTO> getRecentSales(int limit);
}

