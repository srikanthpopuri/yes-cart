package org.yes.cart.service.order.impl.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProcessAllocationOrderEventHandlerImpl implements OrderEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private final WarehouseService warehouseService;
    private final SkuWarehouseService skuWarehouseService;

    /**
     * Construct transition.
     *
     * @param warehouseService    warehouse service
     * @param skuWarehouseService sku on warehouse service to change quantity
     */
    public ProcessAllocationOrderEventHandlerImpl(final WarehouseService warehouseService, final SkuWarehouseService skuWarehouseService) {
        this.warehouseService = warehouseService;
        this.skuWarehouseService = skuWarehouseService;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) {
        synchronized (OrderEventHandler.syncMonitor) {
            try {
                reserveQuantity(orderEvent.getCustomerOrderDelivery());//TOdO all
                return true;
            } catch (Exception e) {
                LOG.error(e.getMessage());
                return false;
            }
        }
    }

    /**
     * Allocate sku quantity on warehouses, that belong to shop, where order was made.
     *
     * @param orderDelivery reserve for this delivery
     * @throws Exception in case if can not allocate quantity for each sku
     */
    void reserveQuantity(final CustomerOrderDelivery orderDelivery) throws Exception {


        final Collection<CustomerOrderDeliveryDet> deliveryDetails = orderDelivery.getDetail();

        final List<Warehouse> warehouses = warehouseService.findByShopId(
                orderDelivery.getCustomerOrder().getShop().getShopId());


        for (CustomerOrderDeliveryDet det : deliveryDetails) {
            ProductSku productSku = det.getSku();
            BigDecimal toAllocate = det.getQty();
            for (Warehouse warehouse : warehouses) {
                skuWarehouseService.voidReservation(warehouse, productSku, toAllocate);

                toAllocate = skuWarehouseService.debit(warehouse, productSku, toAllocate);
                if (toAllocate.equals(BigDecimal.ZERO)) {
                    break; // quantity allocated
                }
            }
            if (MoneyUtils.isFirstBiggerThanSecond(toAllocate, BigDecimal.ZERO)) {
                throw new Exception("ProcessAllocationOrderEventHandlerImpl. Can not allocate total qty = " + det.getQty()
                        + " for sku = " + productSku.getCode()
                        + " in delivery " + orderDelivery.getDevileryNum());
            }
        }
        orderDelivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

    }

    /**
     * Get warehouse service.
     *
     * @return {@link WarehouseService}
     */
    protected WarehouseService getWarehouseService() {
        return warehouseService;
    }

    /**
     * Get sku warehouse service.
     *
     * @return {@link SkuWarehouseService}
     */
    protected SkuWarehouseService getSkuWarehouseService() {
        return skuWarehouseService;
    }
}