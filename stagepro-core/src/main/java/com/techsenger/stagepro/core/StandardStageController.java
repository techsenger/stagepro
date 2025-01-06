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

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;

/**
 *
 * @author Pavel Castornii
 */
public class StandardStageController extends SimpleStageController {

    private final Button minimizeButton = new Button();

    private final MaximizeButton maximizeButton = new MaximizeButton(MaximizeButton.ResizableStatePolicy.VISIBILITY);

    private boolean buttonBoxListenerEnabled = true;

    public StandardStageController(Stage stage, double width, double height) {
        this(stage, width, height, true);
    }

    public StandardStageController(Stage stage, double width, double height, boolean initTitleBar) {
        super(stage, width, height, false);
        build();
        bind();
        addListeners();
        addHandlers();
        if (initTitleBar) {
            getButtonBox().getChildren().addAll(minimizeButton, maximizeButton, getCloseButton());
            getTitleBar().getChildren().addAll(getIconView(), getTitleLabel(), new Spacer(), getButtonBox());
        }
    }

    public Button getMinimizeButton() {
        return minimizeButton;
    }

    public MaximizeButton getMaximizeButton() {
        return maximizeButton;
    }

    private void build() {
        this.minimizeButton.getStyleClass().add("minimize-button");
        getButtonBox().getChildren().addListener((ListChangeListener<? super Node>) (e) -> {
            if (this.buttonBoxListenerEnabled) {
                this.maximizeButton.setIndex(getButtonBox().getChildren().indexOf(this.maximizeButton));
                checkMaximizeButton();
            }
        });
    }

    private void bind() {
        getResizer().disabledProperty().bind(getStage().maximizedProperty()
                .or(Bindings.not(getStage().resizableProperty())));
    }

    private void addListeners() {
        getStage().resizableProperty().addListener((ov, oldV, newV) -> checkMaximizeButton());
    }

    private void addHandlers() {
        minimizeButton.setOnAction(e -> getStage().setIconified(true));
        maximizeButton.setOnAction(e -> getStage().setMaximized(!getStage().isMaximized()));
        getTitleBar().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getClickCount() == 2) {
                getStage().setMaximized(!getStage().isMaximized());
                e.consume();
            }
        });
    }

    private void checkMaximizeButton() {
        if (this.maximizeButton.getIndex() == -1) {
            return;
        }
        if (maximizeButton.getPolicy() == MaximizeButton.ResizableStatePolicy.INTERACTIVITY) {
            maximizeButton.setDisable(!getStage().isResizable());
        } else {
            this.buttonBoxListenerEnabled = false;
            if (getStage().isResizable()) {
                if (this.getMaximizeButton().getParent() != getButtonBox()) {
                    getButtonBox().getChildren().add(maximizeButton.getIndex(), maximizeButton);
                }
            } else {
                if (this.getMaximizeButton().getParent() == getButtonBox()) {
                    getButtonBox().getChildren().remove(maximizeButton.getIndex());
                }
            }
            this.buttonBoxListenerEnabled = true;
        }
    }
}

