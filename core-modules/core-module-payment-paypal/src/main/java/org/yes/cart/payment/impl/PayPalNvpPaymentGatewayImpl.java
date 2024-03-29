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

import com.paypal.sdk.core.nvp.NVPDecoder;
import com.paypal.sdk.core.nvp.NVPEncoder;
import com.paypal.sdk.exceptions.PayPalException;
import com.paypal.sdk.profiles.APIProfile;
import com.paypal.sdk.profiles.ProfileFactory;
import com.paypal.sdk.services.NVPCallerServices;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.yes.cart.payment.PaymentGatewayInternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.util.ShopCodeContext;

import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PayPalNvpPaymentGatewayImpl extends AbstractPayPalPaymentGatewayImpl implements PaymentGatewayInternalForm {

    private NVPCallerServices nvpCallerServices = null;

    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            true, true, true, false,
            true, true, true,
            false, true, false,
            null,
            false, true
    );


    private NVPCallerServices getNvpCallerServices() {
        if (nvpCallerServices == null) {
            APIProfile profile;

            try {
                profile = ProfileFactory.createSignatureAPIProfile();
                profile.setAPIUsername(getProperties().getProperty(PP_API_USER_NAME));
                profile.setAPIPassword(getProperties().getProperty(PP_API_USER_PASSWORD));
                profile.setSignature(getProperties().getProperty(PP_SIGNATURE));
                //profile.setCertificateFile(getProperties().getProperty(PP_KEY_PATH));
                //profile.setPrivateKeyPassword(getProperties().getProperty(PP_KEY_PASSWORD));
                profile.setEnvironment(getProperties().getProperty(PP_ENVIRONMENT));
                nvpCallerServices = new NVPCallerServices();
                nvpCallerServices.setAPIProfile(profile);
            } catch (PayPalException e) {
                ShopCodeContext.getLog(this).error("Cant create api profile", e);
            }


        }
        return nvpCallerServices;
    }


    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "payPalNvpPaymentGateway";
    }

    /**
     * {@inheritDoc}
     */
    public synchronized PaymentGatewayFeature getPaymentGatewayFeatures() {
        return paymentGatewayFeature;
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorize(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH);
        final NVPEncoder encoder = createAuthRequest(payment, "Authorization");
        return runTransaction(encoder, payment, AUTH);
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorizeCapture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH_CAPTURE);
        final NVPEncoder encoder = createAuthRequest(payment, "Sale");
        return runTransaction(encoder, payment, AUTH_CAPTURE);
    }


    /**
     * {@inheritDoc}
     */
    public Payment reverseAuthorization(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REVERSE_AUTH);
        final NVPEncoder encoder = new NVPEncoder();
        encoder.add("METHOD", "DoVoid");
        encoder.add("TRXTYPE", "V");
        encoder.add("AUTHORIZATIONID", payment.getTransactionAuthorizationCode());
        return runTransaction(encoder, payment, REVERSE_AUTH);
    }

    /**
     * Void capture perfomred as refund
     * <p/>
     * {@inheritDoc}
     */
    public Payment voidCapture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(VOID_CAPTURE);
        final NVPEncoder encoder = new NVPEncoder();
        //encoder.add("METHOD", "DoVoid");
        //encoder.add("TRXTYPE", "V");
        //encoder.add("AUTHORIZATIONID", payment.getTransactionAuthorizationCode());

        encoder.add("METHOD", "RefundTransaction");
        encoder.add("REFUNDTYPE", "Full");
        encoder.add("TRANSACTIONID", payment.getTransactionReferenceId());

        return runTransaction(encoder, payment, VOID_CAPTURE);

    }

    /**
     * {@inheritDoc}
     */
    public Payment refund(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REFUND);
        final NVPEncoder encoder = new NVPEncoder();
        encoder.add("METHOD", "RefundTransaction");
        encoder.add("REFUNDTYPE", "Full");
        encoder.add("TRANSACTIONID", payment.getTransactionReferenceId());
        return runTransaction(encoder, payment, REFUND);

    }


    /**
     * {@inheritDoc}
     */
    public Payment capture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(CAPTURE);
        final NVPEncoder encoder = new NVPEncoder();
        encoder.add("METHOD", "DoCapture");
        encoder.add("TRXTYPE", "D");
        encoder.add("AUTHORIZATIONID", payment.getTransactionAuthorizationCode());
        encoder.add("AMT", payment.getPaymentAmount().setScale(2, RoundingMode.HALF_UP).toString());
        encoder.add("COMPLETETYPE", "NotComplete");
        encoder.add("CURRENCYCODE", payment.getOrderCurrency());
        return runTransaction(encoder, payment, CAPTURE);

    }


    private NVPEncoder createAuthRequest(final Payment payment, final String paymentAction) {
        final NVPEncoder encoder = new NVPEncoder();
        encoder.add("METHOD", "DoDirectPayment");
        encoder.add("PAYMENTACTION", paymentAction);
        encoder.add("AMT", payment.getPaymentAmount().setScale(2, RoundingMode.HALF_UP).toString());
        encoder.add("CREDITCARDTYPE", payment.getCardType());
        encoder.add("ACCT", payment.getCardNumber());
        encoder.add("EXPDATE",
                payment.getCardExpireMonth()
                        + payment.getCardExpireYear());
        encoder.add("CVV2", payment.getCardCvv2Code());

        encoder.add("FIRSTNAME", payment.getBillingAddress().getFirstname());
        encoder.add("LASTNAME", payment.getBillingAddress().getLastname());
        encoder.add("STREET",
                getStreetAddress(payment.getBillingAddress().getAddrline1(), payment.getBillingAddress().getAddrline2())
        );
        encoder.add("CITY", payment.getBillingAddress().getCity());
        encoder.add("STATE", payment.getBillingAddress().getStateCode());
        encoder.add("ZIP", payment.getBillingAddress().getStateCode());
        encoder.add("COUNTRYCODE", payment.getBillingAddress().getCountryCode());
        encoder.add("CURRENCYCODE", payment.getOrderCurrency());
        return encoder;
    }

    private String maskCardNumber(final String stringWithCardnumber, final String cardNumber) {
        if (StringUtils.isBlank(cardNumber)) {
            return stringWithCardnumber;
        }
        return stringWithCardnumber.replace(cardNumber, "ZXCV-ZXCV-ZXCV-ZXCV");
    }

    private Payment runTransaction(final NVPEncoder requestEncoder, final Payment payment, final String operation) {
        final Logger log = ShopCodeContext.getLog(this);
        payment.setTransactionOperation(operation);
        String request = StringUtils.EMPTY;
        String responce = StringUtils.EMPTY;
        try {
            request = requestEncoder.encode();

            if (log.isDebugEnabled()) {
                log.debug("Request is {}", request);
            }

            responce = getNvpCallerServices().call(request);

            if (log.isDebugEnabled()) {
                log.debug("Response is {}", responce);
            }

            NVPDecoder nvpDecoder = new NVPDecoder();
            nvpDecoder.decode(responce);


            final String strAck = nvpDecoder.get("ACK");
            if (strAck != null && !(strAck.equals("Success") || strAck.equals("SuccessWithWarning"))) {
                payment.setTransactionOperationResultCode(nvpDecoder.get("L_ERRORCODE0"));
                payment.setTransactionOperationResultMessage(responce);
                payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                payment.setPaymentProcessorBatchSettlement(false);
            } else {
                final String transactionId = nvpDecoder.get("TRANSACTIONID");

                if (log.isDebugEnabled()) {
                    log.debug("Transaction Id is {}", responce);
                }

                payment.setTransactionReferenceId(transactionId);
                payment.setTransactionAuthorizationCode(transactionId);
                payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
                payment.setPaymentProcessorBatchSettlement(CAPTURE.equals(operation) || AUTH_CAPTURE.equals(operation));
            }
        } catch (PayPalException e) {
            String message = MessageFormat.format(
                    "Can not run transaction\n request = {0}\n responce = {1}\n payment = {2}. Original message {3}",
                    maskCardNumber(request, payment.getCardNumber()),
                    maskCardNumber(responce, payment.getCardNumber()),
                    payment,
                    e.getMessage()
            );
            log.error(message, e);
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
            payment.setPaymentProcessorBatchSettlement(false);
            payment.setTransactionOperationResultMessage(message);
        }
        return payment;
    }

    /**
     * Create properties for cs client.
     *
     * @return propersties.
     */
    Properties getProperties() {
        final Properties props = new Properties();
        for (String paramLabel : new String[]{
                PP_API_USER_NAME, PP_API_USER_PASSWORD,
                PP_SIGNATURE,
                PP_ENVIRONMENT
                /*PP_KEY_PASSWORD, PP_KEY_PATH,
                PP_ENVIRONMENT*/
        }) {
            final String value = getParameterValue(paramLabel);
            if (value != null) {
                props.put(paramLabel, value);
            }
        }
        return props;
    }
}
