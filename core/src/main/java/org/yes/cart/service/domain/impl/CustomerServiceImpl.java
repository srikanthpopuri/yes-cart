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

package org.yes.cart.service.domain.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.customer.CustomerNameFormatter;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.HashHelper;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.utils.impl.AttrValueRankComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerServiceImpl extends BaseGenericServiceImpl<Customer> implements CustomerService {

    private final HashHelper passwordHashHelper;

    private final GenericDAO<Object, Long> customerShopDao;

    private final AttributeService attributeService;

    private final ShopService shopService;

    private final CustomerNameFormatter customerNameFormatter;


    /**
     * Construct customer service.
     *
     * @param genericDao customer dao to use.
     * @param passwordHashHelper to generate password hash
     * @param customerShopDao    to delete
     * @param shopService shop service (reuse retrieval + caching)
     * @param customerNameFormatter customer name formatter
     */
    public CustomerServiceImpl(final GenericDAO<Customer, Long> genericDao,
                               final HashHelper passwordHashHelper,
                               final GenericDAO<Object, Long> customerShopDao,
                               final AttributeService attributeService,
                               final ShopService shopService,
                               final CustomerNameFormatter customerNameFormatter) {
        super(genericDao);
        this.passwordHashHelper = passwordHashHelper;
        this.customerShopDao = customerShopDao;
        this.attributeService = attributeService;
        this.shopService = shopService;
        this.customerNameFormatter = customerNameFormatter;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "customerService-customerByEmail")
    public Customer getCustomerByEmail(final String email) {
        Customer customer = getGenericDao().findSingleByCriteria(Restrictions.eq("email", email));
        if (customer != null) {
            Hibernate.initialize(customer.getAttributes());
            for (AttrValueCustomer attrValueCustomer :  customer.getAttributes()) {
                Hibernate.initialize(attrValueCustomer.getAttribute().getEtype());
            }
        }
        return customer;
    }

    /**
     * {@inheritDoc}
     */
    public Customer getCustomerByToken(final String token) {
        Customer customer = getGenericDao().findSingleByCriteria(Restrictions.eq("authToken", token));
        if (customer != null) {
            Hibernate.initialize(customer.getAttributes());
            for (AttrValueCustomer attrValueCustomer :  customer.getAttributes()) {
                Hibernate.initialize(attrValueCustomer.getAttribute().getEtype());
            }
        }
        return customer;
    }

    /**
     * {@inheritDoc}
     */
    public Customer getCustomerByPublicKey(final String publicKey, final String lastName) {
        Customer customer = getGenericDao().findSingleByCriteria(Restrictions.eq("publicKey", publicKey), Restrictions.eq("lastname", lastName));
        if (customer != null) {
            Hibernate.initialize(customer.getAttributes());
            for (AttrValueCustomer attrValueCustomer :  customer.getAttributes()) {
                Hibernate.initialize(attrValueCustomer.getAttribute().getEtype());
            }
        }
        return customer;
    }

    /**
     * {@inheritDoc}
     */
    public List<Shop> getCustomerShopsByEmail(final String email) {
        Customer customer = getGenericDao().findSingleByCriteria(Restrictions.eq("email", email));
        final List<Shop> shops = new ArrayList<Shop>();
        if (customer != null) {
            for (CustomerShop customerShop : customer.getShops()) {
                final Shop shop = shopService.getById(customerShop.getShop().getShopId());
                if (shop != null) {
                    shops.add(shop);
                }
            }
        }
        return shops;
    }

    /**
     * {@inheritDoc}
     */
    public String formatNameFor(final Customer customer, final Shop shop) {

        if (shop != null) {
            final AttrValue format = shop.getAttributeByCode(AttributeNamesKeys.Shop.CUSTOMER_NAME_FORMATTER);
            if (format != null) {
                return customerNameFormatter.formatName(customer, format.getVal());
            }
        }
        return customerNameFormatter.formatName(customer);

    }

    /**
     * {@inheritDoc}
     */
    public List<Customer> findCustomer(final String email, final String firstname,
                                       final String lastname, final String middlename, final String tag) {

        final List<Criterion> criterionList = new ArrayList<Criterion>();

        if (StringUtils.isNotBlank(email)) {
            criterionList.add(Restrictions.like("email", email, MatchMode.ANYWHERE));
        }

        if (StringUtils.isNotBlank(firstname)) {
            criterionList.add(Restrictions.like("firstname", firstname, MatchMode.ANYWHERE));
        }

        if (StringUtils.isNotBlank(lastname)) {
            criterionList.add(Restrictions.like("lastname", lastname, MatchMode.ANYWHERE));
        }

        if (StringUtils.isNotBlank(middlename)) {
            criterionList.add(Restrictions.like("middlename", middlename, MatchMode.ANYWHERE));
        }

        if (StringUtils.isNotBlank(tag)) {
            criterionList.add(Restrictions.like("tag", tag, MatchMode.ANYWHERE));
        }

        if (criterionList.isEmpty()) {
            return getGenericDao().findAll();

        } else {
            return getGenericDao().findByCriteria(
                    criterionList.toArray(new Criterion[criterionList.size()])
            );

        }


    }

    /**
     * {@inheritDoc}
     */
    public boolean isCustomerExists(final String email) {
        final List<Object> counts = (List) getGenericDao().findQueryObjectByNamedQuery("EMAIL.CHECK", email);
        if (CollectionUtils.isNotEmpty(counts)) {
            return ((Number) counts.get(0)).intValue() > 0;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPasswordValid(final String email, final String password) {
        try {
            final List<Criterion> criterionList = new ArrayList<Criterion>();

            final String hash = passwordHashHelper.getHash(password);

            criterionList.add(Restrictions.eq("email", email));
            criterionList.add(Restrictions.eq("password", hash));

            final List<Customer> customer = getGenericDao().findByCriteria(
                    criterionList.toArray(new Criterion[criterionList.size()])
            );

            return (customer != null && customer.size() == 1);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Add new attribute to customer. If attribute already exists, his value will be changed.
     * This method not perform any actions to persist changes.
     *
     * @param customer       given customer
     * @param attributeCode  given attribute code
     * @param attributeValue given attribute value
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, key = "#customer.email")
    public void addAttribute(final Customer customer, final String attributeCode, final String attributeValue) {
        if (StringUtils.isNotBlank(attributeValue)) {
            AttrValueCustomer attrVal = customer.getAttributeByCode(attributeCode);
            if (attrVal != null) {
                attrVal.setVal(attributeValue);
            } else {
                Attribute attr = attributeService.findByAttributeCode(attributeCode);
                Hibernate.initialize(attr.getEtype()); //load lazy values
                if (attr != null) {
                    attrVal = getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
                    attrVal.setVal(attributeValue);
                    attrVal.setAttribute(attr);
                    attrVal.setCustomer(customer);
                    customer.getAttributes().add(attrVal);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<AttrValueCustomer> getRankedAttributeValues(final Customer customer) {
        final List<String> filledAttributes;
        final List<AttrValueCustomer> rez;
        if (customer != null) {
            rez = new ArrayList<AttrValueCustomer>(customer.getAttributes());
            filledAttributes = new ArrayList<String>(customer.getAttributes().size());
            for (AttrValueCustomer attrVal : customer.getAttributes()) {
                filledAttributes.add(attrVal.getAttribute().getCode());
                rez.add(attrVal);
            }
        } else {
            rez = new ArrayList<AttrValueCustomer>();
            filledAttributes = Collections.EMPTY_LIST;
        }
        final List<Attribute> emptyAttributes = attributeService.findAvailableAttributes(AttributeGroupNames.CUSTOMER, filledAttributes);
        for (Attribute attr : emptyAttributes) {
            AttrValueCustomer attrValueCustomer = attributeService.getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
            attrValueCustomer.setAttribute(attr);
            attrValueCustomer.setCustomer(customer);
            rez.add(attrValueCustomer);
        }
        Collections.sort(rez, new AttrValueRankComparator());
        for (AttrValueCustomer avc : rez) {
            Hibernate.initialize(avc.getAttribute().getEtype()); //load lazy values
        }
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
        "customerService-customerByEmail"
    }, allEntries = false, key = "#customer.email")
    public Customer create(final Customer customer, final Shop shop) {
        if (shop != null) {
            final CustomerShop customerShop = getGenericDao().getEntityFactory().getByIface(CustomerShop.class);
            customerShop.setCustomer(customer);
            customerShop.setShop(shop);
            customer.getShops().add(customerShop);
        }
        return super.create(customer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer create(final Customer instance) {
        throw new UnsupportedOperationException("Please use create(final Customer customer, final Shop shop)");
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, key = "#email")
    public Customer update(final String email, final String shopCode) {
        final Shop shop = shopService.getShopByCode(shopCode);
        if (shop != null) {
            final Customer customer = getCustomerByEmail(email);
            for (final CustomerShop customerShop : customer.getShops()) {
                if (shop.getShopId() == customerShop.getShop().getShopId()) {
                    return customer;
                }
            }
            final CustomerShop customerShop = getGenericDao().getEntityFactory().getByIface(CustomerShop.class);
            customerShop.setCustomer(customer);
            customerShop.setShop(shop);
            customer.getShops().add(customerShop);
            return super.update(customer);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, key = "#customer.email")
    public void resetPassword(final Customer customer, final Shop shop, final String authToken) {
        super.update(customer);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, key = "#instance.email")
    public Customer update(final Customer instance) {
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, key = "#instance.email")
    public void delete(final Customer instance) {
        for(CustomerShop cshop : instance.getShops()) {
            customerShopDao.delete(cshop);
        }
        super.delete(instance);
    }
}
