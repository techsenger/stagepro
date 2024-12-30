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

    private final MaximizeButton maximizeButton = new MaximizeButton(MaximizeButton.ResizableStatePolicy.VISIBILITY, 1);

    private final ChangeListener<? super Boolean> stageMaximizedListener =
            (ov, oldV, newV) -> checkMaximizedState(newV);

    private final ChangeListener<? super Boolean> stageResizableListener =
            (ov, oldV, newV) -> checkResizableState(newV);

    public StandardStageController(Stage stage, double width, double height) {
        super(stage, width, height);
    }

    public Button getMinimizeButton() {
        return minimizeButton;
    }

    public MaximizeButton getMaximizeButton() {
        return maximizeButton;
    }

    @Override
    protected void preInitialize() {
        getButtonBox().getChildren().addAll(minimizeButton, maximizeButton, getCloseButton());
        getTitleBar().getChildren().addAll(getIcon(), getTitle(), new Spacer(), getButtonBox());
    }

    @Override
    protected void build() {
        super.build();
        this.minimizeButton.getStyleClass().add("minimize-button");
        checkMaximizedState(getStage().maximizedProperty().get());
        checkResizableState(getStage().resizableProperty().get());
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        getStage().maximizedProperty().addListener(stageMaximizedListener);
        getStage().resizableProperty().addListener(stageResizableListener);
    }

    @Override
    protected void addHandlers() {
        super.addHandlers();
        minimizeButton.setOnAction(e -> getStage().setIconified(true));
        maximizeButton.setOnAction(e -> getStage().setMaximized(!getStage().isMaximized()));
        getTitleBar().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getClickCount() == 2) {
                getStage().setMaximized(!getStage().isMaximized());
                e.consume();
            }
        });
    }

    @Override
    protected void removeListeners() {
        super.removeListeners();
        getStage().maximizedProperty().removeListener(stageMaximizedListener);
        getStage().resizableProperty().removeListener(stageResizableListener);
    }

    private void checkMaximizedState(boolean maximized) {
        maximizeButton.pseudoClassStateChanged(toggledClass, maximized);
        getResizer().disabledProperty().set(maximized);
    }

    private void checkResizableState(boolean resizable) {
        if (maximizeButton.getPolicy() == MaximizeButton.ResizableStatePolicy.INTERACTIVITY) {
            maximizeButton.setDisable(!resizable);
        } else {
            if (resizable) {
                if (this.getMaximizeButton().getParent() != getButtonBox()) {
                    getButtonBox().getChildren().add(maximizeButton.getIndex(), maximizeButton);
                }
            } else {
                if (this.getMaximizeButton().getParent() == getButtonBox()) {
                    getButtonBox().getChildren().remove(maximizeButton.getIndex());
                }
            }
        }
        getResizer().disabledProperty().set(!resizable);
    }
}

