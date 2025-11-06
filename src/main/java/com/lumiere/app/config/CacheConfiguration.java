package com.lumiere.app.config;

import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Caffeine caffeine = jHipsterProperties.getCache().getCaffeine();

        CaffeineConfiguration<Object, Object> caffeineConfiguration = new CaffeineConfiguration<>();
        caffeineConfiguration.setMaximumSize(OptionalLong.of(caffeine.getMaxEntries()));
        caffeineConfiguration.setExpireAfterWrite(OptionalLong.of(TimeUnit.SECONDS.toNanos(caffeine.getTimeToLiveSeconds())));
        caffeineConfiguration.setStatisticsEnabled(true);
        jcacheConfiguration = caffeineConfiguration;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.lumiere.app.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.lumiere.app.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.lumiere.app.domain.User.class.getName());
            createCache(cm, com.lumiere.app.domain.Authority.class.getName());
            createCache(cm, com.lumiere.app.domain.User.class.getName() + ".authorities");
            createCache(cm, com.lumiere.app.domain.Product.class.getName());
            createCache(cm, com.lumiere.app.domain.Product.class.getName() + ".variants");
            createCache(cm, com.lumiere.app.domain.Product.class.getName() + ".reviews");
            createCache(cm, com.lumiere.app.domain.Product.class.getName() + ".questions");
            createCache(cm, com.lumiere.app.domain.Product.class.getName() + ".collections");
            createCache(cm, com.lumiere.app.domain.Product.class.getName() + ".wishlistedBies");
            createCache(cm, com.lumiere.app.domain.ProductVariant.class.getName());
            createCache(cm, com.lumiere.app.domain.Collection.class.getName());
            createCache(cm, com.lumiere.app.domain.Collection.class.getName() + ".products");
            createCache(cm, com.lumiere.app.domain.Customer.class.getName());
            createCache(cm, com.lumiere.app.domain.Customer.class.getName() + ".orders");
            createCache(cm, com.lumiere.app.domain.Customer.class.getName() + ".wishlists");
            createCache(cm, com.lumiere.app.domain.Customer.class.getName() + ".addresses");
            createCache(cm, com.lumiere.app.domain.Customer.class.getName() + ".loyaltyHistories");
            createCache(cm, com.lumiere.app.domain.Customer.class.getName() + ".notifications");
            createCache(cm, com.lumiere.app.domain.Address.class.getName());
            createCache(cm, com.lumiere.app.domain.Orders.class.getName());
            createCache(cm, com.lumiere.app.domain.Orders.class.getName() + ".orderItems");
            createCache(cm, com.lumiere.app.domain.Orders.class.getName() + ".orderStatusHistories");
            createCache(cm, com.lumiere.app.domain.OrderItem.class.getName());
            createCache(cm, com.lumiere.app.domain.OrderStatusHistory.class.getName());
            createCache(cm, com.lumiere.app.domain.Warehouse.class.getName());
            createCache(cm, com.lumiere.app.domain.Inventory.class.getName());
            createCache(cm, com.lumiere.app.domain.StockMovement.class.getName());
            createCache(cm, com.lumiere.app.domain.Voucher.class.getName());
            createCache(cm, com.lumiere.app.domain.FlashSale.class.getName());
            createCache(cm, com.lumiere.app.domain.FlashSale.class.getName() + ".products");
            createCache(cm, com.lumiere.app.domain.FlashSaleProduct.class.getName());
            createCache(cm, com.lumiere.app.domain.LoyaltyTransaction.class.getName());
            createCache(cm, com.lumiere.app.domain.ProductReview.class.getName());
            createCache(cm, com.lumiere.app.domain.ProductQuestion.class.getName());
            createCache(cm, com.lumiere.app.domain.ProductQuestion.class.getName() + ".answers");
            createCache(cm, com.lumiere.app.domain.ProductAnswer.class.getName());
            createCache(cm, com.lumiere.app.domain.Notification.class.getName());
            createCache(cm, com.lumiere.app.domain.StockNotification.class.getName());
            createCache(cm, com.lumiere.app.domain.ChatSession.class.getName());
            createCache(cm, com.lumiere.app.domain.ChatSession.class.getName() + ".messages");
            createCache(cm, com.lumiere.app.domain.ChatMessage.class.getName());
            // jhipster-needle-caffeine-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
