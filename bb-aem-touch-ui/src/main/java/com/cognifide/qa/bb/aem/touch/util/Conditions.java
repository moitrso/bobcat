/*-
 * #%L
 * Bobcat
 * %%
 * Copyright (C) 2016 Cognifide Ltd.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.cognifide.qa.bb.aem.touch.util;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.cognifide.qa.bb.constants.HtmlTags;
import com.cognifide.qa.bb.constants.Timeouts;
import com.cognifide.qa.bb.provider.selenium.BobcatWait;
import com.cognifide.qa.bb.wait.WebElementCallable;
import com.google.inject.Inject;

/**
 * Helper class that provides multiple methods wrapping common wait cases
 */
public class Conditions { //todo remove & move non-deprecated methods to BobcatWait

  @Inject
  private BobcatWait bobcatWait;

  /**
   * Checks if given web element CSS class contains given value
   *
   * @param element {@link WebElement} which CSS class will be examined
   * @param value   string value which method will look for
   * @return true if CSS class contains given value
   */
  @Deprecated //todo remove, ExpectedConditions + HtmlTags cover it
  public boolean classContains(WebElement element, String value) {
    return hasAttributeWithValue(element, HtmlTags.Attributes.CLASS, value);
  }

  /**
   * Checks if {@link ExpectedCondition} given in method parameter is met in small timeout (5
   * seconds)
   *
   * @param condition {@link ExpectedCondition} instance that will be examined
   * @return true if the condition is met without violating the timeout
   */
  public boolean isConditionMet(ExpectedCondition condition) {
    return isConditionMet(condition, Timeouts.SMALL);
  }

  /**
   * Checks if {@link ExpectedCondition} given in method parameter is met in given timeout
   *
   * @param condition {@link ExpectedCondition} instance that will be examined
   * @param timeout   timeout limit for the condition examination
   * @return true if condition is met in given timeout
   */
  public boolean isConditionMet(ExpectedCondition condition, int timeout) {
    boolean result = true;
    try {
      verify(condition, timeout);
    } catch (TimeoutException | StaleElementReferenceException e) {
      result = false;
    }
    return result;
  }

  /**
   * Checks if {@link ExpectedCondition} given in method parameter is met in small timeout (5
   * seconds)
   *
   * @param condition {@link ExpectedCondition} instance  hat will be examined
   * @return true if the condition is met without violating the timeout
   */
  public <T> T verify(ExpectedCondition<T> condition) {
    return verify(condition, Timeouts.SMALL);
  }

  /**
   * Checks if {@link ExpectedCondition} given in method parameter is met in given timeout
   *
   * @param condition {@link ExpectedCondition} instance that will be examined
   * @param timeout   timeout limit for the condition examination
   * @return true if condition is met in given timeout
   */
  public <T> T verify(ExpectedCondition<T> condition, int timeout) {
    return bobcatWait.withTimeout(timeout).until(condition);
  }

  /**
   * Verifies that author mode is loaded (author loader is hidden) and then verifies if given
   * condition is met
   * in medium timeout (15 seconds)
   *
   * @param condition condition that has to be met
   */
  @Deprecated //todo change it to an expected condition, remove the method from the API
  public void verifyPostAjax(
      ExpectedCondition condition) {
    //authorLoader.verifyIsHidden();
    verify(condition, Timeouts.MEDIUM);
  }

  /**
   * Checks if a WebElement is ready to be operated on, i.e. is visible and not stale and returns that element
   *
   * @param element WebElement to be checked
   * @return checked element
   */
  public WebElement elementReady(WebElement element) {
    return bobcatWait.withTimeout(Timeouts.MEDIUM).until(ignored -> {
      try {
        return element.isDisplayed() ? element : null;
      } catch (StaleElementReferenceException e) {
        return null;
      }
    });
  }

  /**
   * Wraps a WebElement's method - executes it in a StaleReferenceElementException-safe way.
   *
   * @param element         element from which method will be invoked
   * @param elementCallable method to be wrapped
   * @param <T>             type of the returned value
   * @return result from the called method
   */
  public <T> T staleSafe(WebElement element, WebElementCallable<T> elementCallable) {
    return verify(ignored -> {
      try {
        return elementCallable.call(element);
      } catch (StaleElementReferenceException e) {
        return null;
      }
    }, Timeouts.MEDIUM);
  }

  /**
   * Examines if element has attribute value like one passed in parameter.
   *
   * @param element   {@link WebElement} instance that is going to be examined.
   * @param attribute attribute which value will be tested.
   * @param value     expected value of the element attribute
   * @return true if the element has attribute value like one passed in parameter.
   */
  @Deprecated //todo remove, ExpectedConditions already contains such condition
  public boolean hasAttributeWithValue(final WebElement element, final String attribute, final String value) {
    boolean result = true;
    try {
      bobcatWait.withTimeout(Timeouts.SMALL).until(input -> element.getAttribute(attribute).contains(value));
    } catch (TimeoutException e) {
      result = false;
    }
    return result;
  }

}
