package br.edu.utfpr.tsi.utfparking.domain.notification.model;

import lombok.Getter;

@Getter
public enum TopicApplication {
    ROOT("/utfparking"), NOTIFICATION("/notifications"), RECOGNIZER("/new-recognizer"), CONFIG("/change-config");

    private final String topicName;

    TopicApplication(String topicName) {
        this.topicName = topicName;
    }
}
