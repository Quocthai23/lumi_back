package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Orders;
import com.lumiere.app.domain.enumeration.OrderStatus;
import com.lumiere.app.repository.CustomerRepository;
import com.lumiere.app.repository.OrderItemRepository;
import com.lumiere.app.repository.OrdersRepository;
import com.lumiere.app.service.DashboardService;
import com.lumiere.app.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for Dashboard statistics.
 */
@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final OrdersRepository ordersRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;

    // Các trạng thái đơn hàng được tính vào doanh thu và thống kê
    private static final List<OrderStatus> COMPLETED_STATUSES = Arrays.asList(
        OrderStatus.DELIVERED,
        OrderStatus.COMPLETED,
        OrderStatus.CONFIRMED,
        OrderStatus.PROCESSING,
        OrderStatus.SHIPPING
    );

    public DashboardServiceImpl(
        OrdersRepository ordersRepository,
        CustomerRepository customerRepository,
        OrderItemRepository orderItemRepository
    ) {
        this.ordersRepository = ordersRepository;
        this.customerRepository = customerRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        LOG.debug("Request to get dashboard stats");

        // Tính thời gian: tháng hiện tại
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(now);
        Instant startOfMonth = currentMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

        // Tổng doanh thu tháng hiện tại
        BigDecimal totalRevenue = ordersRepository.sumTotalAmountByStatusAndDateRange(
            COMPLETED_STATUSES,
            startOfMonth,
            endOfMonth
        );
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        // Số khách hàng mới tháng hiện tại
        List<Object[]> newCustomersThisMonth = customerRepository.getNewCustomersByMonth(now.getYear());
        Long subscriptions = 0L;
        for (Object[] row : newCustomersThisMonth) {
            if (row[1] != null && ((Number) row[1]).intValue() == now.getMonthValue()) {
                subscriptions = ((Number) row[2]).longValue();
                break;
            }
        }

        // Tổng số đơn hàng tháng hiện tại
        Long sales = ordersRepository.countByStatusAndDateRange(
            COMPLETED_STATUSES,
            startOfMonth,
            endOfMonth
        );
        if (sales == null) {
            sales = 0L;
        }

        // Số đơn hàng đang xử lý (active now)
        Instant startOfToday = now.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfToday = now.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
        Long activeNow = ordersRepository.countByStatusAndDateRange(
            Arrays.asList(OrderStatus.CONFIRMED, OrderStatus.PROCESSING, OrderStatus.SHIPPING),
            startOfToday,
            endOfToday
        );
        if (activeNow == null) {
            activeNow = 0L;
        }

        return new DashboardStatsDTO(totalRevenue, subscriptions, sales, activeNow);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyRevenueDTO> getRevenueByMonth() {
        LOG.debug("Request to get revenue by month");

        int currentYear = LocalDate.now().getYear();
        List<Object[]> results = ordersRepository.getRevenueByMonth(COMPLETED_STATUSES, currentYear);

        // Tạo map từ kết quả query
        Map<Integer, BigDecimal> revenueMap = new HashMap<>();
        for (Object[] row : results) {
            Integer month = ((Number) row[1]).intValue();
            BigDecimal total = (BigDecimal) row[2];
            revenueMap.put(month, total);
        }

        // Tạo danh sách cho 12 tháng
        List<MonthlyRevenueDTO> monthlyRevenues = new ArrayList<>();
        String[] monthNames = { "Thg 1", "Thg 2", "Thg 3", "Thg 4", "Thg 5", "Thg 6", 
                                "Thg 7", "Thg 8", "Thg 9", "Thg 10", "Thg 11", "Thg 12" };

        for (int i = 1; i <= 12; i++) {
            BigDecimal total = revenueMap.getOrDefault(i, BigDecimal.ZERO);
            monthlyRevenues.add(new MonthlyRevenueDTO(monthNames[i - 1], total));
        }

        return monthlyRevenues;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyCustomerDTO> getNewCustomersByMonth() {
        LOG.debug("Request to get new customers by month");

        int currentYear = LocalDate.now().getYear();
        List<Object[]> results = customerRepository.getNewCustomersByMonth(currentYear);

        // Tạo map từ kết quả query
        Map<Integer, Long> customerMap = new HashMap<>();
        for (Object[] row : results) {
            Integer month = ((Number) row[1]).intValue();
            Long count = ((Number) row[2]).longValue();
            customerMap.put(month, count);
        }

        // Tạo danh sách cho 12 tháng
        List<MonthlyCustomerDTO> monthlyCustomers = new ArrayList<>();
        String[] monthNames = { "Thg 1", "Thg 2", "Thg 3", "Thg 4", "Thg 5", "Thg 6", 
                                "Thg 7", "Thg 8", "Thg 9", "Thg 10", "Thg 11", "Thg 12" };

        for (int i = 1; i <= 12; i++) {
            Long count = customerMap.getOrDefault(i, 0L);
            monthlyCustomers.add(new MonthlyCustomerDTO(monthNames[i - 1], count));
        }

        return monthlyCustomers;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProductDTO> getTopProducts(int limit) {
        LOG.debug("Request to get top products with limit: {}", limit);

        org.springframework.data.domain.Page<Object[]> page = orderItemRepository.getTopProductsByQuantity(
            COMPLETED_STATUSES,
            PageRequest.of(0, limit)
        );

        return page.getContent().stream()
            .map(row -> {
                String name = (String) row[0];
                Long total = ((Number) row[1]).longValue();
                return new TopProductDTO(name, total);
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecentSaleDTO> getRecentSales(int limit) {
        LOG.debug("Request to get recent sales with limit: {}", limit);

        List<Orders> orders = ordersRepository.findRecentOrders(
            COMPLETED_STATUSES,
            PageRequest.of(0, limit)
        );

        return orders.stream()
            .map(order -> {
                String name = "Khách hàng";
                String email = "N/A";

                if (order.getCustomer() != null) {
                    // Lấy tên khách hàng
                    String firstName = order.getCustomer().getFirstName();
                    String lastName = order.getCustomer().getLastName();
                    if (firstName != null || lastName != null) {
                        name = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
                    }

                    // Lấy email từ user
                    if (order.getCustomer().getUser() != null) {
                        email = order.getCustomer().getUser().getEmail();
                        if (email == null) {
                            email = order.getCustomer().getUser().getLogin();
                        }
                    }
                }

                return new RecentSaleDTO(name, email, order.getTotalAmount());
            })
            .collect(Collectors.toList());
    }
}

