package com.myMoneyTracker.util;

import com.myMoneyTracker.model.category.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tudor.grigoriu on 07.08.2016.
 */
public class CategoryUtil {

    private static final String FOOD_AND_BEVERAGES_CATEGORY = "Food & Beverages";
    private static final String CLOTHING_BEAUTY_CATEGORY = "Clothing & Beauty";
    private static final String LIVING_CATEGORY_CATEGORY = "Living";
    private static final String TRANSPORTATION_CATEGORY = "Transportation";
    private static final String ENTERTAINMENT_CATEGORY = "Entertainment";
    private static final String MEDICAL_CATEGORY = "Medical";
    private static final String CAR_MOTOR_CATEGORY_CATEGORY = "Car/Motor";
    private static final String OTHER_CATEGORY = "Other";

    private static final Category FOOD_AND_BEVERAGES =
            new Category(FOOD_AND_BEVERAGES_CATEGORY, "stable");
    private static final Category CLOTHING_AND_BEAUTY =
            new Category(CLOTHING_BEAUTY_CATEGORY, "royal");
    private static final Category LIVING =
            new Category(LIVING_CATEGORY_CATEGORY, "calm");
    private static final Category TRANSPORTATION =
            new Category(TRANSPORTATION_CATEGORY, "balanced");
    private static final Category ENTERTAINMENT =
            new Category(ENTERTAINMENT_CATEGORY, "energized");
    private static final Category MEDICAL =
            new Category(MEDICAL_CATEGORY, "positive");
    private static final Category CAR_MOTOR =
            new Category(CAR_MOTOR_CATEGORY_CATEGORY, "assertive");
    private static final Category OTHER =
            new Category(OTHER_CATEGORY, "dark");

    public static final List<Category> DEFAULT_CATEGORIES= new ArrayList<Category>()
            {
                {
                    add(FOOD_AND_BEVERAGES);
                    add(CLOTHING_AND_BEAUTY);
                    add(LIVING);
                    add(TRANSPORTATION);
                    add(ENTERTAINMENT);
                    add(MEDICAL);
                    add(CAR_MOTOR);
                    add(OTHER);
                }
            };
}
