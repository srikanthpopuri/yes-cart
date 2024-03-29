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

package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.AttributeDTOImpl")]
public class AttributeDTOImpl {

    public var code:String;

    public var mandatory:Boolean;

    public var val:String;

    public var name:String ;

    public var displayNames:Object;

    public var description:String;

    public var etypeId:Number;

    public var etypeName:String ;

    public var attributeId:Number ;

    public var attributegroupId:Number;

    public var allowduplicate:Boolean;

    public var allowfailover:Boolean;

    public var regexp:String;

    public var validationFailedMessage:Object;

    public var rank:int;

    public var choiceData:Object;



    public function AttributeDTOImpl() {
    }


    public function toString():String {
        return "AttributeDTOImpl{code=" + String(code) +
               ",mandatory=" + String(mandatory) +
               ",name=" + String(name) +
               ",etypeName=" + String(etypeName) +
               ",regexp=" + String(regexp) +
               ",attributegroupId=" + String(attributegroupId) +
               ",allowduplicate=" + String(allowduplicate) +
               ",allowfailover=" + String(allowfailover) + "}";
    }
}
}