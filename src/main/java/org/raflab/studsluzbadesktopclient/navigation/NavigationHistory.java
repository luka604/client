package org.raflab.studsluzbadesktopclient.navigation;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

@Component
public class NavigationHistory {

    private final Deque<String> backStack = new ArrayDeque<>();
    private final Deque<String> forwardStack = new ArrayDeque<>();
    private String currentPage = null;
    @Setter
    private String selectedBrojIndeksa = null;

    @Value("${navigation.history.max-depth:10}")
    private int maxHistoryDepth = 10;

    public void navigateTo(String fxml) {
        if (currentPage != null && !currentPage.equals(fxml)) {
            backStack.push(currentPage);
            if (backStack.size() > maxHistoryDepth) {
                ((ArrayDeque<String>) backStack).removeLast();
            }
            forwardStack.clear();
        }
        currentPage = fxml;
    }

    public String goBack() {
        if (backStack.isEmpty()) {
            return null;
        }
        forwardStack.push(currentPage);
        currentPage = backStack.pop();
        return currentPage;
    }

    public String goForward() {
        if (forwardStack.isEmpty()) {
            return null;
        }
        backStack.push(currentPage);
        currentPage = forwardStack.pop();
        return currentPage;
    }



    public boolean canGoBack() {
        return !backStack.isEmpty();
    }

    public boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    public String consumeSelectedBrojIndeksa() {
        String value = selectedBrojIndeksa;
        selectedBrojIndeksa = null;
        return value;
    }
}
