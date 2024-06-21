package com.example.app_epi;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoadingManager {

    private static LoadingManager instance;
    private Stage loadingStage;

    private LoadingManager() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("loading.fxml"));
            Parent root = loader.load();
            loadingStage = new Stage();
            loadingStage.initModality(Modality.APPLICATION_MODAL);
            loadingStage.initStyle(StageStyle.TRANSPARENT); // Tornar a janela transparente
            Scene scene = new Scene(root);
            scene.setFill(null); // Definir a cena como transparente
            loadingStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LoadingManager getInstance() {
        if (instance == null) {
            instance = new LoadingManager();
        }
        return instance;
    }

    public void showLoading() {
        Platform.runLater(() -> loadingStage.show());
    }

    public void hideLoading() {
        Platform.runLater(() -> loadingStage.hide());
    }

    public void runTaskWithLoading(Runnable task) {
        showLoading();
        Task<Void> backgroundTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                task.run();
                return null;
            }

            @Override
            protected void succeeded() {
                hideLoading();
            }

            @Override
            protected void failed() {
                hideLoading();
                // Trate falhas aqui, se necessário
            }
        };

        new Thread(backgroundTask).start();
    }
}
