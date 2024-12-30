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

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * A stage with a title and a close button.
 *
 * @author Pavel Castornii
 */
public class SimpleStageController extends EmptyStageController {

    private final Label icon = new Label();

    private final Label title = new Label();

    private final Button closeButton = new Button();

    public SimpleStageController(Stage stage, double width, double height) {
        super(stage, width, height);
    }

    public Label getIcon() {
        return icon;
    }

    public Label getTitle() {
        return title;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    @Override
    protected void preInitialize() {
        getButtonBox().getChildren().add(closeButton);
        getTitleBar().getChildren().addAll(icon, title, new Spacer(), getButtonBox());
    }

    @Override
    protected void build() {
        super.build();
        this.icon.getStyleClass().add("icon");
        this.title.getStyleClass().add("title");
        this.closeButton.getStyleClass().add("close-button");
        this.closeButton.setFocusTraversable(false);
    }

    @Override
    protected void bind() {
        super.bind();
        this.title.textProperty().bindBidirectional(getStage().titleProperty());
    }

    @Override
    protected void addHandlers() {
        super.addHandlers();
        this.closeButton.setOnAction(e -> getStage().close());
    }

    @Override
    protected void unbind() {
        super.unbind();
        this.title.textProperty().unbindBidirectional(getStage().titleProperty());
    }
}
