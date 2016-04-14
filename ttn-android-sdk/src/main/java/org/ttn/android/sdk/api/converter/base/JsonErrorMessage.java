package org.ttn.android.sdk.api.converter.base;

/*
 * Copyright 2015 The Things Network
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by fabiotiriticco on 25/09/15.
 *
 */
public class JsonErrorMessage {
    public static String unexpectedObject(Class expectedObj, Object foundObj) {
        return "Wrong object. Expected "
                + expectedObj.getSimpleName()
                + ", found "
                + foundObj.getClass().getSimpleName()
                + ".";
    }

    public static String missingMandatoryValue(String field) {
        return "Missing mandatory value: " + field;
    }
}
