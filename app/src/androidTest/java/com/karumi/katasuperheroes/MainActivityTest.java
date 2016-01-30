/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.matchers.RecyclerViewItemsCountMatcher;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.ui.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    private static final String TEST_HERO_IMAGE =
            "https://avatars0.githubusercontent.com/u/878293?v=3&s=460";
    public static final String TEST_HERO_NAME = "AwesomeHero";
    public static final int TEST_NUMBER_SUPERHERO = 10;

    @Rule public DaggerMockRule<MainComponent> daggerRule =
            new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
                    new DaggerMockRule.ComponentSetter<MainComponent>() {
                        @Override
                        public void setComponent(MainComponent component) {
                            SuperHeroesApplication app =
                                    (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                                            .getTargetContext()
                                            .getApplicationContext();
                            app.setComponent(component);
                        }
                    });

    @Rule public IntentsTestRule<MainActivity> activityRule =
            new IntentsTestRule<>(MainActivity.class, true, false);

    @Mock SuperHeroesRepository repository;

    @Test
    public void showsEmptyCaseIfThereAreNoSuperHeroes() {
        givenThereAreNoSuperHeroes();

        startActivity();

        onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
    }

    @Test
    public void showsListWhenThereAreSuperHeroes() {
        givenThereAreSomeSuperHeroes(1, false);

        startActivity();

        onView(withText("¯\\_(ツ)_/¯")).check(matches(not(isDisplayed())));
        onView(withId(R.id.iv_super_hero_photo)).check(matches(isDisplayed()));
    }

    @Test
    public void showsListWithOnlyOneHero() {
        givenThereAreSomeSuperHeroes(1, false);

        startActivity();

        onView(withId(R.id.recycler_view)).check(matches(RecyclerViewItemsCountMatcher.recyclerViewHasItemCount(1)));
    }

    @Test
    public void showsAvengerBadgeWhenAvengerHero() {
        givenThereAreSomeSuperHeroes(1, true);

        startActivity();

        onView(withId(R.id.recycler_view)).check(matches(RecyclerViewItemsCountMatcher.recyclerViewHasItemCount(1)));
        onView(withId(R.id.iv_avengers_badge)).check(matches(isDisplayed()));
    }

    @Test
    public void showsListWithSomeSuperHeroes() {
        List<SuperHero> superHeroes = givenThereAreSomeSuperHeroes(1000, true);

        startActivity();

        int i = 0;
        for (SuperHero hero : superHeroes) {
            onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.scrollToPosition(i++));
            onView(withText(hero.getName())).check(matches(isDisplayed()));
        }
    }

    private void givenThereAreNoSuperHeroes() {
        when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
    }

    private List<SuperHero> givenThereAreSomeSuperHeroes(int numberOfHeroes, boolean hasAvengers) {
        List<SuperHero> superHeroes = new ArrayList<>();
        for (int i = 1; i <= numberOfHeroes; i++) {
            SuperHero hero = new SuperHero(
                    getHeroName(i),
                    TEST_HERO_IMAGE,
                    hasAvengers,
                    "Some Description " + i);
            superHeroes.add(hero);
        }

        when(repository.getAll()).thenReturn(superHeroes);
        return superHeroes;
    }

    private String getHeroName(int position) {
        return TEST_HERO_NAME + " " + position;
    }

    private MainActivity startActivity() {
        return activityRule.launchActivity(null);
    }
}