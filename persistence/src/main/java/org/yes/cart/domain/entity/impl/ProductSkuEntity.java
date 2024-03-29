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
package org.yes.cart.domain.entity.impl;


import org.apache.commons.lang.StringUtils;
import org.hibernate.search.annotations.*;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.bridge.ProductBridge;
import org.yes.cart.domain.entity.bridge.SkuPriceBridge;
import org.yes.cart.domain.interceptor.ProductSkuEntityIndexingInterceptor;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */

@Indexed(index = "luceneindex/productsku", interceptor = ProductSkuEntityIndexingInterceptor.class)
@ClassBridge(impl = SkuPriceBridge.class)
public class ProductSkuEntity implements org.yes.cart.domain.entity.ProductSku, java.io.Serializable {

    private long skuId;
    private long version;

    private String code;
    private String manufacturerCode;
    private String name;
    private String displayName;
    private String description;
    private Product product;
    private int rank;
    private String barCode;
    private Collection<SkuPrice> skuPrice = new ArrayList<SkuPrice>(0);
    private Collection<AttrValueProductSku> attributes = new ArrayList<AttrValueProductSku>(0);
    private SeoEntity seoInternal;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductSkuEntity() {
    }



    @Fields({
            @Field(name = "sku.code", index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES),
            @Field(name = "sku.code_stem", index = Index.YES, analyze = Analyze.YES, norms = Norms.NO, store = Store.NO)
    })
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Fields({
            @Field(name = "sku.manufacturerCode", index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES),
            @Field(name = "sku.manufacturerCode_stem", index = Index.YES, analyze = Analyze.YES, norms = Norms.NO, store = Store.NO)
    })
    public String getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Fields({
            @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES,
                    bridge = @FieldBridge(impl = org.yes.cart.domain.entity.bridge.DisplayNameBridge.class)),
            @Field(name = "displayNameAsIs", index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES,
                    bridge = @FieldBridge(impl = org.yes.cart.domain.entity.bridge.DisplayNameBridge.class))
    })
    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Field(name = "productId", index = Index.YES, analyze = Analyze.NO, store = Store.YES,
            bridge = @FieldBridge(impl = ProductBridge.class))
    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getBarCode() {
        return this.barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Field(bridge = @FieldBridge(impl = org.yes.cart.domain.entity.bridge.AttributeValueBridge.class))
    public Collection<AttrValueProductSku> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Collection<AttrValueProductSku> attributes) {
        this.attributes = attributes;
    }

    public SeoEntity getSeoInternal() {
        return this.seoInternal;
    }

    public void setSeoInternal(SeoEntity seo) {
        this.seoInternal = seo;
    }

    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @DocumentId
    public long getSkuId() {
        return this.skuId;
    }

    public long getId() {
        return this.skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    public Collection<AttrValueProductSku> getAttributesByCode(final String attributeCode) {
        final Collection<AttrValueProductSku> result = new ArrayList<AttrValueProductSku>();
        if (attributeCode != null && this.attributes != null) {
            for (AttrValueProductSku attrValue : this.attributes) {
                if (attrValue.getAttribute().getCode().equals(attributeCode)) {
                    result.add(attrValue);
                }
            }
        }
        return result;
    }

    public AttrValueProductSku getAttributeByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        if (this.attributes != null) {
            for (AttrValueProductSku attrValue : this.attributes) {
                if (attrValue.getAttribute().getCode().equals(attributeCode)) {
                    return attrValue;
                }
            }
        }
        return null;
    }


    public Map<String, AttrValue> getAllAttributesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<String, AttrValue>();
        if (this.attributes != null) {
            for (AttrValue attrValue : this.attributes) {
                if (attrValue != null && attrValue.getAttribute() != null) {
                    rez.put(attrValue.getAttribute().getCode(), attrValue);
                }
            }
        }
        return rez;
    }

    public Collection<AttrValue> getAllAttributes() {
        return new ArrayList<AttrValue>(attributes);
    }


    public Seo getSeo() {
        SeoEntity seo = getSeoInternal();
        if (seo == null) {
            seo = new SeoEntity();
            this.setSeoInternal(seo);
        }
        return seo;
    }

    public void setSeo(final Seo seo) {
        this.setSeoInternal((SeoEntity) seo);
    }

    /**
     * Get default image, which is stored into lucene index, to reduce db hit.
     * @return default product image if found, otherwise no image constant.
     */
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public String getDefaultImage() {
        final AttrValue attr = getAttributeByCode(AttributeNamesKeys.Product.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "0");
        if (attr == null || StringUtils.isBlank(attr.getVal())) {

            return Constants.NO_IMAGE;

        }
        return attr.getVal();
    }


}


