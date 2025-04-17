/*
 * Copyright 2024-2025 Pavel Castornii.
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

import com.techsenger.toolkit.fx.StageResizer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Base stage controller: no title icon, label and buttons. Use this controller for creating non standard stages.
 *
 * @author Pavel Castornii
 */
public class BaseStageController {

    private static final PseudoClass MAXIMIZED_PSEUDO_CLASS = PseudoClass.getPseudoClass("maximized");

    private final Stage stage;

    private final double width;

    private final double height;

    private final HBox titleBar = new HBox();

    private final HBox buttonBox = new HBox();

    private final VBox contentArea = new VBox();

    private final VBox stageBox = new VBox(titleBar, contentArea);

    private final ObjectProperty<Node> content = new SimpleObjectProperty<>();

    private double pressedX;

    private double pressedY;

    private double pressedMouseX;

    private double pressedMouseY;

    private StageResizer resizer;

    /**
     * Calling {@link Stage#initStyle(javafx.stage.StageStyle)} on a visible stage will throw an
     * {@link java.lang.IllegalStateException}: "Cannot set style once stage has been made visible."
     *
     * <p>Additionally, to remove a StagePro stage, it is necessary to call {@link Stage#hide()}.
     * For this reason, adding a deinitialize method for the controller is unnecessary. If you need
     * to remove this controller and use a standard JavaFX stage, simply create a new stage instance.
     */
    public BaseStageController(Stage stage, double width, double height) {
        this.stage = stage;
        this.width = width;
        this.height = height;
        build();
        bind();
        addListeners();
        addHandlers();
    }

    public ObjectProperty<Node> contentProperty() {
        return content;
    }

    public Node getContent() {
        return this.content.get();
    }

    public void setContent(Node content) {
        this.content.set(content);
    }

    public HBox getTitleBar() {
        return this.titleBar;
    }

    public HBox getButtonBox() {
        return buttonBox;
    }

    public Stage getStage() {
        return stage;
    }

    protected StageResizer getResizer() {
        return resizer;
    }

    /**
     * Returns the stage box that is the root of the scene.
     *
     * @return
     */
    protected VBox getStageBox() {
        return stageBox;
    }

    private void build() {
        //with StageStyle.UNDECORATED resizing works slowly, besides with UNDECORATED style background bahind radius
        //corners will be visible
        this.stage.initStyle(StageStyle.TRANSPARENT);
        this.stage.setWidth(width);
        this.stage.setHeight(height);
        var scene = new Scene(this.stageBox);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        this.resizer = new StageResizer(this.stage.minWidthProperty(), this.stage.minHeightProperty(),
                this.stage.maxWidthProperty(), this.stage.maxHeightProperty(), (e) -> onResizingStarted(e),
                (e) -> onResizingFinished(e));
        this.resizer.initialize(stage);
        VBox.setVgrow(stageBox, Priority.ALWAYS);
        //there are different stylesheet priorities for scene and node
        scene.getStylesheets().add(BaseStageController.class.getResource("stage.css").toExternalForm());
        this.stageBox.getStyleClass().add("stage-box");
        VBox.setVgrow(this.contentArea, Priority.ALWAYS);
        this.contentArea.getStyleClass().add("content-area");
        this.titleBar.getStyleClass().add("title-bar");
        this.buttonBox.getStyleClass().add("button-box");
        checkMaximizedPseudoClass(getStage().maximizedProperty().get());
        setEmptyContent();
    }

    private void bind() {
        this.resizer.disabledProperty()
                .bind(this.stage.maximizedProperty().or(Bindings.not(this.stage.resizableProperty())));
    }

    private void addListeners() {
        this.content.addListener((ov, oldValue, newV) -> {
            if (newV != null) {
                setNewContent(newV);
            } else {
                setEmptyContent();
            }
        });
        this.stage.maximizedProperty().addListener((ov, oldV, newV) -> checkMaximizedPseudoClass(newV));
    }

    private void addHandlers() {
        this.titleBar.setOnMousePressed((event) -> this.doOnTitleBarMousePressed(event));
        this.titleBar.setOnMouseDragged((event) -> this.doOnTitleBarMouseDragged(event));
    }

    private void doOnTitleBarMousePressed(MouseEvent event) {
        this.pressedMouseX = event.getScreenX();
        this.pressedMouseY = event.getScreenY();
        this.pressedX = this.stage.getX();
        this.pressedY = this.stage.getY();
        event.consume();
    }

    private void doOnTitleBarMouseDragged(MouseEvent event) {
        var mouseXDiff = event.getScreenX() - this.pressedMouseX;
        var mouseYDiff = event.getScreenY() - this.pressedMouseY;
        var newX = this.pressedX + mouseXDiff;
        var newY = this.pressedY + mouseYDiff;
        //it seems that javafx checks valid positions itself
        this.stage.setX(newX);
        this.stage.setY(newY);
        event.consume();
    }

    private void setNewContent(Node content) {
        this.contentArea.getChildren().clear();
        if (content != null) {
            VBox.setVgrow(content, Priority.ALWAYS);
            this.contentArea.getChildren().add(content);
        } else {
            setEmptyContent();
        }
    }

    private void setEmptyContent() {
        var label = new Label("No Content");
        var node = new StackPane(label);
        setNewContent(node);
    }

    private void onResizingStarted(MouseEvent mouseEvent) {
        var event = new StageResizeEvent(StageResizeEvent.STAGE_RESIZE_STARTED, mouseEvent);
        this.stage.fireEvent(event);
    }

    private void onResizingFinished(MouseEvent mouseEvent) {
        var event = new StageResizeEvent(StageResizeEvent.STAGE_RESIZE_FINISHED, mouseEvent);
        this.stage.fireEvent(event);
    }

    private void checkMaximizedPseudoClass(boolean maximized) {
        this.stageBox.pseudoClassStateChanged(MAXIMIZED_PSEUDO_CLASS, maximized);
    }
}
