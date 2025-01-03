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

package com.techsenger.stagepro.sampler;

import com.techsenger.stagepro.core.SimpleStageController;
import com.techsenger.stagepro.core.EmptyStageController;
import com.techsenger.stagepro.core.MaximizeButton;
import com.techsenger.stagepro.core.StageResizeEvent;
import com.techsenger.stagepro.core.Spacer;
import com.techsenger.stagepro.core.StandardStageController;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Pavel Castornii
 */
public class Sampler extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ObservableList<Sample> samples = FXCollections.observableArrayList(createSamples());
        TableView<Sample> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setItems(samples);

        TableColumn<Sample, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        idColumn.setMaxWidth(50);
        idColumn.setMinWidth(50);
        idColumn.setResizable(false);

        TableColumn<Sample, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDescription()));

        TableColumn<Sample, Runnable> actionColumn = new TableColumn<>("Action");
        actionColumn.getStyleClass().add("action");
        actionColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAction()));
        actionColumn.setMaxWidth(100);
        actionColumn.setMinWidth(100);
        actionColumn.setResizable(false);
        actionColumn.setCellFactory(column -> {
            TableCell<Sample, Runnable> cell = new TableCell<>() {

                private final Button button = new Button("Run");

                {
                    button.setOnAction(event -> {
                        Sample sample = getTableRow().getItem();
                        if (sample != null) {
                            sample.getAction().run();
                        }
                    });
                    button.setPadding(new Insets(3, 6, 3, 6));
                    setPadding(new Insets(2));
                }

                @Override
                protected void updateItem(Runnable sample, boolean empty) {
                    super.updateItem(sample, empty);
                    if (sample == null || empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(button);
                    }
                }
            };
            return cell;
        });

        tableView.getColumns().addAll(idColumn, descriptionColumn, actionColumn);

        VBox root = new VBox(tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        Scene scene = new Scene(root, 800, 400);
        scene.getStylesheets().add(Sampler.class.getResource("sampler.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("StagePro Sampler");
        primaryStage.show();
    }

    private List<Sample> createSamples() {
        return List.of(
                createSample1(),
                createSample2(),
                createSample3(),
                createSample4(),
                createSample5(),
                createSample6(),
                createSample7()
        );
    }

    private Sample createSample1() {
        return new Sample(1, "Empty Stage: resize handlers", () -> {
            var stage = new Stage();
            var controller = new EmptyStageController(stage, 800, 550);
            var button = new Button("Close");
            button.setOnAction(e -> stage.close());
            var content = new StackPane(button);
            controller.setContent(content);
            stage.addEventHandler(StageResizeEvent.STAGE_RESIZING_STARTED,
                    e -> System.out.println("Resizing started"));
            stage.addEventHandler(StageResizeEvent.STAGE_RESIZING_FINISHED,
                    e -> System.out.println("Resizing finished"));
            stage.show();
        });
    }

    private Sample createSample2() {
        return new Sample(2, "Simple Stage: icon and text on the left, close button on the right", () -> {
            var stage = new Stage();
            var controller = new SimpleStageController(stage, 800, 550);
            setIconAndText(controller);
            stage.show();
        });
    }

    private Sample createSample3() {
        return new Sample(3, "Standard Stage: icon and text on the left, three buttons on the right", () -> {
            var stage = new Stage();
            var controller = new StandardStageController(stage, 800, 550);
            setIconAndText(controller);
            setStateTestContent(stage, controller);
            stage.show();
        });
    }

    private Sample createSample4() {
        return new Sample(4, "Standard Stage: icon and text on the left, three buttons on the right, size effect",
                () -> {
            var stage = new Stage();
            var controller = new StandardStageController(stage, 800, 550);
            setIconAndText(controller);
            controller.setSizeEffectEnabled(true);
            stage.show();
        });
    }

    private Sample createSample5() {
        return new Sample(5, "Standard Stage: three buttons, text on the left", () -> {
            var stage = new Stage();
            class LeftStandardStageController extends StandardStageController {

                LeftStandardStageController(Stage stage, double width, double height) {
                    super(stage, width, height, false);
                    getButtonBox().getChildren().addAll(getCloseButton(), getMinimizeButton(), getMaximizeButton());
                    getTitleBar().getChildren().addAll(getButtonBox(), new Spacer(10), getTitle(),
                            new Spacer());
                }
            }
            var controller = new LeftStandardStageController(stage, 800, 550);
            setIconAndText(controller);
            setStateTestContent(stage, controller);
            stage.show();
        });
    }

    private Sample createSample6() {
        return new Sample(6, "Standard Stage: icon and text on the left, three buttons on the right, dark theme",
                () -> {
            var stage = new Stage();
            var controller = new StandardStageController(stage, 800, 550);
            setIconAndText(controller);
            setStateTestContent(stage, controller);
            controller.setDarkTheme(true);
            stage.getScene().getStylesheets().add(Sampler.class.getResource("dark-theme.css").toExternalForm());
            stage.show();
        });
    }

    private Sample createSample7() {
        return new Sample(7, "Standard Stage: icon and menu on the left, three buttons on the right", () -> {
            var stage = new Stage();
            class LeftStandardStageController extends StandardStageController {

                private final Menu fileMenu = new Menu("_File");

                private final Menu editMenu = new Menu("_Edit");

                private final Menu helptMenu = new Menu("_Help");

                private final MenuBar menuBar = new MenuBar(fileMenu, editMenu, helptMenu);

                LeftStandardStageController(Stage stage, double width, double height) {
                    super(stage, width, height, false);
                    this.menuBar.setStyle("-fx-background-color: #CCCCCC;");
                    getButtonBox().getChildren().addAll(getMinimizeButton(), getMaximizeButton(), getCloseButton());
                    getTitleBar().getChildren().addAll(getIcon(), menuBar, new Spacer(), getButtonBox());
                }
            }
            var controller = new LeftStandardStageController(stage, 800, 550);
            setIconAndText(controller);
            setStateTestContent(stage, controller);
            stage.show();
        });
    }

    private void setIconAndText(SimpleStageController controller) {
        controller.getIcon().setText("\u26A1");
        controller.getIcon().setStyle("-fx-font-size:20;-fx-font-weight:bold");
        controller.getTitle().setText("Title");
    }

    private void setStateTestContent(Stage stage, StandardStageController controller) {
        var policyButton = new Button("Update max policy");
        policyButton.setOnAction(e -> {
            if (controller.getMaximizeButton().getPolicy() == MaximizeButton.ResizableStatePolicy.INTERACTIVITY) {
                controller.getMaximizeButton().setPolicy(MaximizeButton.ResizableStatePolicy.VISIBILITY);
            } else {
                controller.getMaximizeButton().setPolicy(MaximizeButton.ResizableStatePolicy.INTERACTIVITY);
            }
        });
        var resizableButton = new Button("Update resizable");
        resizableButton.setOnAction(e -> stage.setResizable(!stage.isResizable()));
        var content = new HBox(policyButton, resizableButton);
        content.setAlignment(Pos.CENTER);
        content.setSpacing(10);
        controller.setContent(content);
    }

}
