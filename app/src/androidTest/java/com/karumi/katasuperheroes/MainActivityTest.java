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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.matchers.RecyclerViewItemsCountMatcher;
import com.karumi.katasuperheroes.matchers.ToolbarMatcher;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.recyclerview.RecyclerViewInteraction;
import com.karumi.katasuperheroes.ui.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.cosenonjaviste.daggermock.DaggerMockRule;

@RunWith(AndroidJUnit4.class) @LargeTest public class MainActivityTest {

  @Rule public DaggerMockRule<MainComponent> daggerRule =
      new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
          new DaggerMockRule.ComponentSetter<MainComponent>() {
            @Override public void setComponent(MainComponent component) {
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

  @Test public void showsEmptyCaseIfThereAreNoSuperHeroes() {
    givenThereAreNoSuperHeroes();

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
  }

  @Test
  public void showOneSuperhero() {
    givenThereAreAnySuperHeroes(1, false);

    startActivity();

    onView(withId(R.id.recycler_view)).check(matches(RecyclerViewItemsCountMatcher.recyclerViewHasItemCount(1)));
  }

  @Test
  public void showNSuperheros() {
    int numberOfSuperheroes = 10;
    givenThereAreAnySuperHeroes(numberOfSuperheroes, false);

    startActivity();

    onView(withId(R.id.recycler_view)).check(matches(RecyclerViewItemsCountMatcher.recyclerViewHasItemCount(numberOfSuperheroes)));
  }

  @Test
  public void showCorrectNameAndNameForEachSuperhero() {
    List<SuperHero> superHeroes = givenThereAreAnySuperHeroes(2, false);

    startActivity();

    RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view))
            .withItems(superHeroes)
            .check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
              @Override public void check(SuperHero superHero, View view, NoMatchingViewException e) {
                matches(hasDescendant(withText(superHero.getName()))).check(view, e);
              }
            });
  }

  @Test
  public void showTitleForTheApp() {
    givenThereAreAnySuperHeroes(5, false);

    startActivity();

    ToolbarMatcher.onToolbarWithTitle(getResourceString(R.string.app_name));
  }

  @Test
  public void hideEmptyTextWhenHaveItems() {
    givenThereAreAnySuperHeroes(5, false);

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(not(isDisplayed())));
  }

  @Test
  public void showAvengersBadge() {
    List<SuperHero> superHeroes = givenThereAreAnySuperHeroes(10, true);

    startActivity();

    RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view))
            .withItems(superHeroes)
            .check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
              @Override public void check(SuperHero superHero, View view, NoMatchingViewException e) {
                  matches(hasDescendant(allOf(withId(R.id.iv_avengers_badge), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))))
                          .check(view, e);
              }
            });
  }

  @Test
  public void hideAvengersBadge() {
    List<SuperHero> superHeroes = givenThereAreAnySuperHeroes(10, false);

    startActivity();

    RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view))
            .withItems(superHeroes)
            .check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
              @Override public void check(SuperHero superHero, View view, NoMatchingViewException e) {
                  matches(hasDescendant(allOf(withId(R.id.iv_avengers_badge), withEffectiveVisibility(ViewMatchers.Visibility.GONE))))
                          .check(view, e);
              }
            });
  }

    @Test
    public void superheroIsOpenOnClick() {
        List<SuperHero> superHeroes = givenThereAreAnySuperHeroes(10, false);

        startActivity();

    }

    private String getResourceString(int id) {
    Context targetContext = InstrumentationRegistry.getTargetContext();
    return targetContext.getResources().getString(id);
  }

  private void givenThereAreNoSuperHeroes() {
    when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
  }

  private List<SuperHero> givenThereAreAnySuperHeroes(int number, boolean isAvenger) {
    List<SuperHero> superHeroes = new ArrayList<>();

    for (int i = 0; i < number; i++) {
      superHeroes.add(new SuperHero("SuperheroName - " + i,
              "https://i.annihil.us/u/prod/marvel/i/mg/9/b0/537bc2375dfb9.jpg", isAvenger,
              "Description for superhero: " + i));
    }

    when(repository.getAll()).thenReturn(superHeroes);

    return superHeroes;
  }

  private MainActivity startActivity() {
    return activityRule.launchActivity(null);
  }
}