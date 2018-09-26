package com.karumi.katasuperheroes.ui.view;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static com.karumi.katasuperheroes.ui.view.SuperHeroDetailActivity.SUPER_HERO_NAME_KEY;

import static org.hamcrest.CoreMatchers.allOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.karumi.katasuperheroes.R;
import com.karumi.katasuperheroes.SuperHeroesApplication;
import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.matchers.ToolbarMatcher;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.DaggerMockRule;

/**
 * Created by Adalberto Plaza on 26/09/2018.
 */
@RunWith(AndroidJUnit4.class) @LargeTest
public class SuperHeroDetailActivityTest {

    @Rule
    public DaggerMockRule<MainComponent> daggerRule =
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

    @Rule public IntentsTestRule<SuperHeroDetailActivity> activityRule =
            new IntentsTestRule<>(SuperHeroDetailActivity.class, true, false);

    @Mock
    SuperHeroesRepository repository;

    @Test
    public void showNameOnToolbar() {
        SuperHero superHero = givenASuperhero(false);

        startActivity();

        ToolbarMatcher.onToolbarWithTitle(superHero.getName());
    }

    @Test
    public void showName() {
        SuperHero superHero = givenASuperhero(false);

        startActivity();

        onView(allOf(withId(R.id.tv_super_hero_name), withText(superHero.getName()))).check(matches(isDisplayed()));
    }

    @Test
    public void showDescription() {
        SuperHero superHero = givenASuperhero(false);

        startActivity();

        onView(withText(superHero.getDescription())).check(matches(isDisplayed()));
    }

    private SuperHero givenASuperhero(boolean isAvenger) {
        SuperHero superHero = new SuperHero("Winter Soldier",
                    "https://i.annihil.us/u/prod/marvel/i/mg/7/40/537bca868687c.jpg", isAvenger,
                    "Olympic-class athlete and exceptional acrobat highly skilled in both unarmed and armed "
                            + "hand-to-hand combat and extremely accurate marksman. he is fluent in four languages "
                            + "including German and Russian.");

        when(repository.getByName(anyString())).thenReturn(superHero);

        return superHero;
    }

    private SuperHeroDetailActivity startActivity() {
        Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), SuperHeroDetailActivity.class);
        intent.putExtra(SUPER_HERO_NAME_KEY, "Winter Soldier");
        return activityRule.launchActivity(intent);
    }
}