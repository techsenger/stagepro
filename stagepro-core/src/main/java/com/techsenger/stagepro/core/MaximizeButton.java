/*
 * Copyright 2024 Pavel Castornii.
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
 */

package com.techsenger.stagepro.core;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 *
 * @author Pavel Castornii
 */
public class MaximizeButton extends Button {

    /**
     * Defines the policy for the behavior of the maximize button in stages where the {@code resizable} property is
     * {@code false}.
     *
     */
    public enum ResizableStatePolicy {

        /**
        * The maximize button's visibility behavior is controlled (i.e., it may be shown or hidden).
        */
       VISIBILITY,

        /**
         * The maximize button's interactivity behavior is controlled (i.e., it may be enabled or disabled).
         */
        INTERACTIVITY
    }

    private final ObjectProperty<ResizableStatePolicy> policy = new SimpleObjectProperty<>();

    /**
     * The index of the button in button box. It is required to add/remove button according to its policy.
     * Button with index -1 will be ignored.
     */
    private final IntegerProperty index = new SimpleIntegerProperty();

    public MaximizeButton() {

    }

    public MaximizeButton(ResizableStatePolicy policy, int index) {
        this(policy, index, null, null);
    }

    public MaximizeButton(ResizableStatePolicy policy, int index, String string) {
        this(policy, index, string, null);
    }

    public MaximizeButton(ResizableStatePolicy policy, int index, String string, Node node) {
        super(string, node);
        this.policy.set(policy);
        this.index.set(index);
        getStyleClass().add("maximize-button");
    }

    public ObjectProperty<ResizableStatePolicy> policyProperty() {
        return policy;
    }

    public ResizableStatePolicy getPolicy() {
        return policy.get();
    }

    public void setPolicy(ResizableStatePolicy policy) {
        this.policy.set(policy);
    }

    public IntegerProperty indexProperty() {
        return index;
    }

    public int getIndex() {
        return index.get();
    }

    public void setIndex(int index) {
        this.index.set(index);
    }
}
