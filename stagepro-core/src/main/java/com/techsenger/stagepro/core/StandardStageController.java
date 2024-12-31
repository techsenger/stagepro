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

import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
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

    /**
     * In JavaFX Button doesn't have selected class.
     */
    private static final PseudoClass toggledClass = PseudoClass.getPseudoClass("toggled");

    private final Button minimizeButton = new Button();

    private final MaximizeButton maximizeButton = new MaximizeButton(MaximizeButton.ResizableStatePolicy.VISIBILITY);

    private boolean buttonBoxListenerEnabled = true;

    private final ChangeListener<? super Boolean> stageMaximizedListener =
            (ov, oldV, newV) -> checkMaximizedState(newV);

    private final ChangeListener<? super Boolean> stageResizableListener =
            (ov, oldV, newV) -> checkResizableState(newV);

    public StandardStageController(Stage stage, double width, double height) {
        this(stage, width, height, true);
    }

    public StandardStageController(Stage stage, double width, double height, boolean initTitleBar) {
        super(stage, width, height, false);
        build();
        addListeners();
        addHandlers();
        if (initTitleBar) {
            getButtonBox().getChildren().addAll(minimizeButton, maximizeButton, getCloseButton());
            getTitleBar().getChildren().addAll(getIcon(), getTitle(), new Spacer(), getButtonBox());
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
        checkMaximizedState(getStage().maximizedProperty().get());
        getButtonBox().getChildren().addListener((ListChangeListener<? super Node>) (e) -> {
            if (this.buttonBoxListenerEnabled) {
                this.maximizeButton.setIndex(getButtonBox().getChildren().indexOf(this.maximizeButton));
                checkResizableState(getStage().isResizable());
            }
        });
    }

    private void addListeners() {
        getStage().maximizedProperty().addListener(stageMaximizedListener);
        getStage().resizableProperty().addListener(stageResizableListener);
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

    private void checkMaximizedState(boolean maximized) {
        maximizeButton.pseudoClassStateChanged(toggledClass, maximized);
        getResizer().disabledProperty().set(maximized);
    }

    private void checkResizableState(boolean resizable) {
        if (this.maximizeButton.getIndex() == -1) {
            return;
        }
        if (maximizeButton.getPolicy() == MaximizeButton.ResizableStatePolicy.INTERACTIVITY) {
            maximizeButton.setDisable(!resizable);
        } else {
            this.buttonBoxListenerEnabled = false;
            if (resizable) {
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
        getResizer().disabledProperty().set(!resizable);
    }
}

