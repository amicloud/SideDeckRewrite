package com.outplaysoftworks.sidedeck;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FirstTest {

    @Rule
    public ActivityTestRule<Splash> mActivityTestRule = new ActivityTestRule<>(Splash.class);

    @Test
    public void firstTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.button2), withText("2"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.button000), withText("000"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.buttonP1Add), withText("+"), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.button2), withText("2"), isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.button000), withText("000"), isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.buttonP1Sub), withText("-"), isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.button2), withText("2"), isDisplayed()));
        appCompatButton7.perform(click());

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.button000), withText("000"), isDisplayed()));
        appCompatButton8.perform(click());

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.buttonP2Add), withText("+"), isDisplayed()));
        appCompatButton9.perform(click());

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.button2), withText("2"), isDisplayed()));
        appCompatButton10.perform(click());

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(R.id.button000), withText("000"), isDisplayed()));
        appCompatButton11.perform(click());

        ViewInteraction appCompatButton12 = onView(
                allOf(withId(R.id.buttonP2Sub), withText("-"), isDisplayed()));
        appCompatButton12.perform(click());

        ViewInteraction appCompatButton13 = onView(
                allOf(withId(R.id.buttonDiceRoll), withText("Dice Roll"), isDisplayed()));
        appCompatButton13.perform(click());

        ViewInteraction appCompatButton14 = onView(
                allOf(withId(R.id.buttonCoinFlip), withText("Coin Flip"), isDisplayed()));
        appCompatButton14.perform(click());

        ViewInteraction appCompatButton15 = onView(
                allOf(withId(R.id.buttonTurn), withText("Turn\n1"), isDisplayed()));
        appCompatButton15.perform(click());

        ViewInteraction appCompatButton16 = onView(
                allOf(withId(R.id.buttonTurn), withText("Turn\n2"), isDisplayed()));
        appCompatButton16.perform(click());

        ViewInteraction appCompatButton17 = onView(
                allOf(withId(R.id.button5), withText("5"), isDisplayed()));
        appCompatButton17.perform(click());

        ViewInteraction appCompatButton18 = onView(
                allOf(withId(R.id.button000), withText("000"), isDisplayed()));
        appCompatButton18.perform(click());

        ViewInteraction appCompatButton19 = onView(
                allOf(withId(R.id.buttonP1Sub), withText("-"), isDisplayed()));
        appCompatButton19.perform(click());

        ViewInteraction appCompatButton20 = onView(
                allOf(withId(R.id.button5), withText("5"), isDisplayed()));
        appCompatButton20.perform(click());

        ViewInteraction appCompatButton21 = onView(
                allOf(withId(R.id.button6), withText("6"), isDisplayed()));
        appCompatButton21.perform(click());

        ViewInteraction appCompatButton22 = onView(
                allOf(withId(R.id.button9), withText("9"), isDisplayed()));
        appCompatButton22.perform(click());

        ViewInteraction appCompatButton23 = onView(
                allOf(withId(R.id.buttonP2Add), withText("+"), isDisplayed()));
        appCompatButton23.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withText("Duel Log"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withText("Calculator"), isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction appCompatButton24 = onView(
                allOf(withId(R.id.buttonTimer), withText("Timer"), isDisplayed()));
        appCompatButton24.perform(click());

        ViewInteraction appCompatButton25 = onView(
                allOf(withId(R.id.buttonStartTimer), withText("Start"), isDisplayed()));
        appCompatButton25.perform(click());

        ViewInteraction appCompatButton26 = onView(
                allOf(withId(R.id.buttonStartTimer), withText("STOP"), isDisplayed()));
        appCompatButton26.perform(click());

        ViewInteraction appCompatButton27 = onView(
                allOf(withId(R.id.buttonResetTimer), withText("Reset"), isDisplayed()));
        appCompatButton27.perform(click());

        ViewInteraction view = onView(
                allOf(withId(R.id.spacerTimerBottom),
                        withParent(withId(R.id.holderTimer)),
                        isDisplayed()));
        view.perform(click());

        ViewInteraction appCompatButton28 = onView(
                allOf(withId(R.id.buttonShowCalc), withText("Calc"), isDisplayed()));
        appCompatButton28.perform(click());

        ViewInteraction appCompatButton29 = onView(
                allOf(withId(R.id.calc0), withText("0"), isDisplayed()));
        appCompatButton29.perform(click());

        ViewInteraction appCompatButton30 = onView(
                allOf(withId(R.id.calc1), withText("1"), isDisplayed()));
        appCompatButton30.perform(click());

        ViewInteraction appCompatButton31 = onView(
                allOf(withId(R.id.calc2), withText("2"), isDisplayed()));
        appCompatButton31.perform(click());

        ViewInteraction appCompatButton32 = onView(
                allOf(withId(R.id.calc3), withText("3"), isDisplayed()));
        appCompatButton32.perform(click());

        ViewInteraction appCompatButton33 = onView(
                allOf(withId(R.id.calc4), withText("4"), isDisplayed()));
        appCompatButton33.perform(click());

        ViewInteraction appCompatButton34 = onView(
                allOf(withId(R.id.calc5), withText("5"), isDisplayed()));
        appCompatButton34.perform(click());

        ViewInteraction appCompatButton35 = onView(
                allOf(withId(R.id.calc6), withText("6"), isDisplayed()));
        appCompatButton35.perform(click());

        ViewInteraction appCompatButton36 = onView(
                allOf(withId(R.id.calc7), withText("7"), isDisplayed()));
        appCompatButton36.perform(click());

        ViewInteraction appCompatButton37 = onView(
                allOf(withId(R.id.calc8), withText("8"), isDisplayed()));
        appCompatButton37.perform(click());

        ViewInteraction appCompatButton38 = onView(
                allOf(withId(R.id.calc9), withText("9"), isDisplayed()));
        appCompatButton38.perform(click());

        ViewInteraction appCompatButton39 = onView(
                allOf(withId(R.id.calcAdd), withText("+"), isDisplayed()));
        appCompatButton39.perform(click());

        ViewInteraction appCompatButton40 = onView(
                allOf(withId(R.id.calcLeftParen), withText("("), isDisplayed()));
        appCompatButton40.perform(click());

        ViewInteraction appCompatButton41 = onView(
                allOf(withId(R.id.calc5), withText("5"), isDisplayed()));
        appCompatButton41.perform(click());

        ViewInteraction appCompatButton42 = onView(
                allOf(withId(R.id.calcRightParen), withText(")"), isDisplayed()));
        appCompatButton42.perform(click());

        ViewInteraction appCompatButton43 = onView(
                allOf(withId(R.id.calcMinus), withText("-"), isDisplayed()));
        appCompatButton43.perform(click());

        ViewInteraction appCompatButton44 = onView(
                allOf(withId(R.id.calc5), withText("5"), isDisplayed()));
        appCompatButton44.perform(click());

        ViewInteraction appCompatButton45 = onView(
                allOf(withId(R.id.calc5), withText("5"), isDisplayed()));
        appCompatButton45.perform(click());

        ViewInteraction appCompatButton46 = onView(
                allOf(withId(R.id.calcEquals), withText("="), isDisplayed()));
        appCompatButton46.perform(click());

        ViewInteraction view2 = onView(
                allOf(withId(R.id.spacerCalculatorBottom),
                        withParent(withId(R.id.holderCalculator)),
                        isDisplayed()));
        view2.perform(click());

        ViewInteraction appCompatButton47 = onView(
                allOf(withId(R.id.buttonReset), withText("Reset"), isDisplayed()));
        appCompatButton47.perform(click());

        ViewInteraction appCompatButton48 = onView(
                allOf(withId(R.id.button2), withText("2"), isDisplayed()));
        appCompatButton48.perform(click());

        ViewInteraction appCompatButton49 = onView(
                allOf(withId(R.id.button00), withText("00"), isDisplayed()));
        appCompatButton49.perform(click());

        ViewInteraction appCompatButton50 = onView(
                allOf(withId(R.id.buttonClear), withText("Clear"), isDisplayed()));
        appCompatButton50.perform(click());

        ViewInteraction appCompatButton51 = onView(
                allOf(withId(R.id.buttonReset), withText("Reset"), isDisplayed()));
        appCompatButton51.perform(click());

        ViewInteraction appCompatButton52 = onView(
                allOf(withId(android.R.id.button1), withText("Yes!"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        appCompatButton52.perform(click());

        ViewInteraction appCompatButton53 = onView(
                allOf(withId(R.id.button1), withText("1"), isDisplayed()));
        appCompatButton53.perform(click());

        ViewInteraction appCompatButton54 = onView(
                allOf(withId(R.id.button6), withText("6"), isDisplayed()));
        appCompatButton54.perform(click());

        ViewInteraction appCompatButton55 = onView(
                allOf(withId(R.id.button6), withText("6"), isDisplayed()));
        appCompatButton55.perform(click());

        ViewInteraction appCompatButton56 = onView(
                allOf(withId(R.id.buttonP2Add), withText("+"), isDisplayed()));
        appCompatButton56.perform(click());

        ViewInteraction appCompatButton57 = onView(
                allOf(withId(R.id.buttonUndo), withText("Undo"), isDisplayed()));
        appCompatButton57.perform(click());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withText("Duel Log"), isDisplayed()));
        appCompatTextView3.perform(click());

        ViewInteraction appCompatTextView4 = onView(
                allOf(withText("Calculator"), isDisplayed()));
        appCompatTextView4.perform(click());

        ViewInteraction appCompatButton58 = onView(
                allOf(withId(R.id.buttonUndo), withText("Undo"), isDisplayed()));
        appCompatButton58.perform(click());

        ViewInteraction appCompatButton59 = onView(
                allOf(withId(R.id.buttonUndo), withText("Undo"), isDisplayed()));
        appCompatButton59.perform(click());

        ViewInteraction appCompatTextView5 = onView(
                allOf(withText("Duel Log"), isDisplayed()));
        appCompatTextView5.perform(click());

        ViewInteraction appCompatTextView6 = onView(
                allOf(withText("Calculator"), isDisplayed()));
        appCompatTextView6.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Overflow"), isDisplayed()));
        appCompatImageButton.perform(click());

    }

}
