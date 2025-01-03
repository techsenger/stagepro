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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Base stage controller: no title icon, label and no buttons. Use this controller for creating non standard stages.
 *
 * @author Pavel Castornii
 */
public class BaseStageController {

    private static final PseudoClass maximizedClass = PseudoClass.getPseudoClass("maximized");

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

    private final BooleanProperty darkTheme = new SimpleBooleanProperty();

    private StageResizer resizer;

    /**
     * Indicates if the size effect is enabled. This effect is shown when the window is dragged
     * to the topmost position of the screen.
     */
    private BooleanProperty sizeEffectEnabled = new SimpleBooleanProperty(false);

    private ObjectProperty<Color> sizeEffectColor = new SimpleObjectProperty<>(Color.web("#00000020"));

    private Stage effectStage;

    private Region effectStageRegion;

    private boolean maximizeOnRelease = false;

    private final Timeline timeline = new Timeline();

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

    public BooleanProperty darkThemeProperty() {
        return darkTheme;
    }

    public boolean isDarkTheme() {
        return this.darkTheme.get();
    }

    public void setDarkTheme(boolean dark) {
        this.darkTheme.set(dark);
    }

    public BooleanProperty sizeEffectEnabledProperty() {
        return sizeEffectEnabled;
    }

    public boolean isSizeEffectEnabled() {
        return sizeEffectEnabled.get();
    }

    public void setSizeEffectEnabled(boolean enabled) {
        sizeEffectEnabled.set(enabled);
    }

    public ObjectProperty<Color> sizeEffectColorProperty() {
        return sizeEffectColor;
    }

    public Color getSizeEffectColor() {
        return sizeEffectColor.get();
    }

    public void setSizeEffectColor(Color color) {
        sizeEffectColor.set(color);
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

    private void addListeners() {
        this.content.addListener((ov, oldValue, newV) -> {
            if (newV != null) {
                setNewContent(newV);
            } else {
                setEmptyContent();
            }
        });
        this.darkTheme.addListener((ov, oldV, newV) -> {
            if (newV) {
                this.stageBox.getStyleClass().add("dark");
            } else {
                this.stageBox.getStyleClass().remove("dark");
            }
        });
        this.stage.maximizedProperty().addListener((ov, oldV, newV) -> checkMaximizedPseudoClass(newV));
    }

    private void addHandlers() {
        this.titleBar.setOnMousePressed((event) -> this.doOnTitleBarMousePressed(event));
        this.titleBar.setOnMouseDragged((event) -> this.doOnTitleBarMouseDragged(event));
        this.titleBar.setOnMouseReleased((event) -> this.doOnTitleBarMouseReleased(event));
    }

    private void doOnTitleBarMousePressed(MouseEvent event) {
        this.maximizeOnRelease = false;
        this.pressedMouseX = event.getScreenX();
        this.pressedMouseY = event.getScreenY();
        this.pressedX = this.stage.getX();
        this.pressedY = this.stage.getY();
        if (this.sizeEffectEnabled.get()) {
            createEffectStage();
        }
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
        if (this.sizeEffectEnabled.get()) {
            var screen = resolveScreen();
            if (event.getScreenY() <= screen.getVisualBounds().getMinY()) {
                if (!this.effectStageRegion.isVisible()) {
                    //important! maximized stage is shown/hidden from/to center, with setMaximized(),
                    //show(), hide() methods depending on OS; that's why we work with node visibility
                    this.effectStage.setX(screen.getVisualBounds().getMinX());
                    this.effectStage.setY(screen.getVisualBounds().getMinY());
                    this.effectStage.setWidth(screen.getVisualBounds().getWidth());
                    this.effectStage.setHeight(screen.getVisualBounds().getHeight());
                    this.effectStageRegion.setVisible(true);
                    this.effectStageRegion.setLayoutX(this.stage.getX() - screen.getVisualBounds().getMinX());
                    this.effectStageRegion.setLayoutY(0);
                    this.effectStageRegion.setPrefWidth(this.stage.getWidth());
                    this.effectStageRegion.setPrefHeight(this.stage.getHeight());
                    this.effectStage.show();
                    this.stage.toFront();
                    this.maximizeOnRelease = true;
                    showEffectAnimation();
                }
            } else {
                this.maximizeOnRelease = false;
                this.effectStageRegion.setVisible(false);
                this.effectStage.setWidth(0);
                this.effectStage.setHeight(0);
            }
        }
        event.consume();
    }

    private void doOnTitleBarMouseReleased(MouseEvent event) {
        if (this.maximizeOnRelease) {
            this.stage.setMaximized(true);
        }
        if (this.effectStage != null) {
            this.effectStage.setWidth(0);
            this.effectStage.setHeight(0);
            this.effectStage.hide();
            this.effectStage = null;
            this.effectStageRegion = null;
        }
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
        var event = new StageResizeEvent(StageResizeEvent.STAGE_RESIZING_STARTED, mouseEvent);
        this.stage.fireEvent(event);
    }

    private void onResizingFinished(MouseEvent mouseEvent) {
        var event = new StageResizeEvent(StageResizeEvent.STAGE_RESIZING_FINISHED, mouseEvent);
        this.stage.fireEvent(event);
    }

    private Screen resolveScreen() {
        Rectangle2D stageBounds = new Rectangle2D(stage.getX(), stage.getY(), 0, 0);
        var screens = Screen.getScreensForRectangle(stageBounds);
        if (!screens.isEmpty()) {
            return screens.get(0);
        } else {
            return Screen.getPrimary();
        }
    }

    private void createEffectStage() {
        if (this.effectStage == null) {
            this.effectStageRegion = new Region();
            this.effectStageRegion.setStyle("-fx-background-color:"
                    + ColorUtils.toHexWithAlpha(sizeEffectColor.get()));
            this.effectStageRegion.setVisible(false);
            var root = new AnchorPane(this.effectStageRegion);
            root.setStyle("-fx-background-color: transparent;");
            var scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            this.effectStage = new Stage();
            this.effectStage.setScene(scene);
            this.effectStage.initStyle(StageStyle.TRANSPARENT);
        }
    }

    private void showEffectAnimation() {
         if (timeline.getStatus() == Timeline.Status.RUNNING) {
            timeline.stop();
        }
        var bigX = 0;
        var bigY = 0;
        var bigWidth = this.effectStage.getWidth();
        var bigHeight = this.effectStage.getHeight();
        var smallX = this.effectStageRegion.getLayoutX();
        var smallY = this.effectStageRegion.getLayoutY();
        var smallWidth = this.effectStageRegion.getPrefWidth();
        var smallHeight = this.effectStageRegion.getPrefHeight();
        var steps = 100;
        for (int i = 0; i <= 100; i++) {
            final int step = i;
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(i * 4),
                e -> {
                    var currentWidth = smallWidth + (bigWidth - smallWidth) * step / steps;
                    var currentHeight = smallHeight + (bigHeight - smallHeight) * step / steps;
                    var currentX = smallX + (bigX - smallX) * step / steps;
                    //var currentY = smallY + (bigY - smallY) * step / steps;
                    this.effectStageRegion.setPrefWidth(currentWidth);
                    this.effectStageRegion.setPrefHeight(currentHeight);
                    this.effectStageRegion.setLayoutX(currentX);
                    //this.effectStageRegion.setLayoutY(currentY);
                }
            );
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void checkMaximizedPseudoClass(boolean maximized) {
        this.stageBox.pseudoClassStateChanged(maximizedClass, maximized);
    }
}
