/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.payment.impl;

import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.ConfigurablePaymentGateway;
import org.yes.cart.payment.service.PaymentGatewayConfigurationVisitor;
import org.yes.cart.payment.service.PaymentGatewayParameterService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 25-Dec-2011
 * Time: 14:12:54
 */
public abstract class AbstractLiqPayPaymentGatewayImpl implements ConfigurablePaymentGateway, PaymentGateway {

    private PaymentGatewayParameterService paymentGatewayParameterService;

    private Collection<PaymentGatewayParameter> allParameters = null;

    private String shopCode;

    /**
     * {@inheritDoc}
     */
    public String getShopCode() {
        return shopCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getName(final String locale) {
        String pgName = getParameterValue("name_" + locale);
        if (pgName == null) {
            pgName = getParameterValue("name");
        }
        if (pgName == null) {
            pgName = getLabel();
        }
        return pgName;
    }


    /**
     * {@inheritDoc}
     */

    public Collection<PaymentGatewayParameter> getPaymentGatewayParameters() {
        if (allParameters == null) {
            allParameters = paymentGatewayParameterService.findAll(getLabel(), shopCode);
        }
        return allParameters;
    }

    /**
     * {@inheritDoc}
     */

    public void deleteParameter(final String parameterLabel) {
        paymentGatewayParameterService.deleteByLabel(getLabel(), parameterLabel);
    }

    /**
     * {@inheritDoc}
     */

    public void addParameter(final PaymentGatewayParameter paymentGatewayParameter) {
        paymentGatewayParameterService.create(paymentGatewayParameter);
    }

    /**
     * {@inheritDoc}
     */

    public void updateParameter(final PaymentGatewayParameter paymentGatewayParameter) {
        paymentGatewayParameterService.update(paymentGatewayParameter);
    }


    /**
     * Parameter service for given gateway.
     *
     * @param paymentGatewayParameterService service
     */
    public void setPaymentGatewayParameterService(
            final PaymentGatewayParameterService paymentGatewayParameterService) {
        this.paymentGatewayParameterService = paymentGatewayParameterService;
    }


    /**
     * Get the parameter value from given collection.
     *
     * @param valueLabel key to search
     * @return value or null if not found
     */
    public String getParameterValue(final String valueLabel) {
        if (valueLabel == null || valueLabel.startsWith("#")) {
            return null; // Need to prevent direct access to Shop specific attributes
        }
        if (shopCode != null && !"DEFAULT".equals(shopCode)) {
            final String shopSpecific = getParameterValueInternal("#" + shopCode + "_" + valueLabel);
            if (shopSpecific != null) {
                return shopSpecific;
            }
        }
        return getParameterValueInternal(valueLabel);
    }

    private String getParameterValueInternal(final String valueLabel) {
        final Collection<PaymentGatewayParameter> values = getPaymentGatewayParameters();
        for (PaymentGatewayParameter keyValue : values) {
            if (keyValue.getLabel().equals(valueLabel)) {
                return keyValue.getValue();
            }
        }
        return null;
    }



    protected String getHiddenField(final String fieldName, final Object value) {
        return "<input type='hidden' name='" + fieldName + "' value='" + value + "'>\n";
    }

    public Map<String, String> setExpressCheckoutMethod(BigDecimal amount, String currencyCode) throws IOException {
        return null;  //nothing
    }

    public Map<String, String> doDoExpressCheckoutPayment(String token, String payerId, BigDecimal amount, String currencyCode) throws IOException {
        return null;  //nothing
    }

    public Map<String, String> getExpressCheckoutDetails(String token) throws IOException {
        return null;  //nothing
    }

    /**
     * Dump map value into String.
     *
     * @param map given map
     * @return dump map as string
     */
    public static String dump(Map<?, ?> map) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append(" : ");
            stringBuilder.append(entry.getValue());
        }

        return stringBuilder.toString();
    }


    /**
     * {@inheritDoc}
     */
    public void accept(final PaymentGatewayConfigurationVisitor visitor) {
        this.shopCode = visitor.getConfiguration("shopCode");
    }


}
