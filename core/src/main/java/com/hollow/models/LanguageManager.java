package com.hollow.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.hollow.models.enums.Language;

import java.util.Locale;

public class LanguageManager {
    private static LanguageManager instance;
    public static I18NBundle bundle;

    private static Array<LanguageObserver> observers = new Array<>();

    private LanguageManager() {
        observers = new Array<>();
    }


    public static LanguageManager getInstance() {
        if (instance == null)
            instance = new LanguageManager();
        return instance;
    }

    public void addObserver(LanguageObserver observer) {
        if (!observers.contains(observer, true)) {
            observers.add(observer);
        }
    }

    public static void removeObserver(LanguageObserver observer) {
        observers.removeValue(observer, true);
    }

    public void load(Language lang) {
        try {
            FileHandle baseFileHandel = Gdx.files.internal("language/strings");

            Locale locale = new Locale(lang.name().toLowerCase());

            bundle = I18NBundle.createBundle(baseFileHandel, locale);
        } catch (Exception e) {
            Gdx.app.error("LanguageManager", "Could not load language bundle: " + e.getMessage());

            bundle = I18NBundle.createBundle(Gdx.files.internal("language/strings"), Locale.ENGLISH);
        }

        for (LanguageObserver observer : observers) {
            observer.onLanguageChanged();
        }
    }

    public String get(String key) {
        if (bundle != null) {
            try {
                return bundle.get(key);
            } catch (java.util.MissingResourceException e) {
                return key;
            }
        }
        return key;
    }
}
