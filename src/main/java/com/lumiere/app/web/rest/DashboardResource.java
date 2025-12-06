package com.lumiere.app.web.rest;

import com.lumiere.app.service.DashboardService;
import com.lumiere.app.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Dashboard statistics.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardResource {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardResource.class);

    private final DashboardService dashboardService;

    public DashboardResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * {@code GET  /dashboard/stats} : Lấy thống kê tổng quan cho dashboard.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dashboard stats.
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        LOG.debug("REST request to get dashboard stats");
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok().body(stats);
    }

    /**
     * {@code GET  /dashboard/revenue-by-month} : Lấy doanh thu theo tháng trong năm hiện tại.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of monthly revenue.
     */
    @GetMapping("/revenue-by-month")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<MonthlyRevenueDTO>> getRevenueByMonth() {
        LOG.debug("REST request to get revenue by month");
        List<MonthlyRevenueDTO> revenue = dashboardService.getRevenueByMonth();
        return ResponseEntity.ok().body(revenue);
    }

    /**
     * {@code GET  /dashboard/new-customers-by-month} : Lấy số lượng khách hàng mới theo tháng trong năm hiện tại.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of monthly customers.
     */
    @GetMapping("/new-customers-by-month")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<MonthlyCustomerDTO>> getNewCustomersByMonth() {
        LOG.debug("REST request to get new customers by month");
        List<MonthlyCustomerDTO> customers = dashboardService.getNewCustomersByMonth();
        return ResponseEntity.ok().body(customers);
    }

    /**
     * {@code GET  /dashboard/top-products} : Lấy top sản phẩm bán chạy nhất.
     *
     * @param limit số lượng sản phẩm cần lấy (mặc định 5)
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of top products.
     */
    @GetMapping("/top-products")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<TopProductDTO>> getTopProducts(@RequestParam(defaultValue = "5") int limit) {
        LOG.debug("REST request to get top products with limit: {}", limit);
        List<TopProductDTO> topProducts = dashboardService.getTopProducts(limit);
        return ResponseEntity.ok().body(topProducts);
    }

    /**
     * {@code GET  /dashboard/recent-sales} : Lấy danh sách đơn hàng gần đây.
     *
     * @param limit số lượng đơn hàng cần lấy (mặc định 5)
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of recent sales.
     */
    @GetMapping("/recent-sales")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<RecentSaleDTO>> getRecentSales(@RequestParam(defaultValue = "5") int limit) {
        LOG.debug("REST request to get recent sales with limit: {}", limit);
        List<RecentSaleDTO> recentSales = dashboardService.getRecentSales(limit);
        return ResponseEntity.ok().body(recentSales);
    }
}

