/*
 * Copyright 2013-2014 Spotify AB. All rights reserved.
 *
 * The contents of this file are licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.spotify.trickle;

import com.google.common.util.concurrent.ListenableFuture;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.google.common.util.concurrent.Futures.immediateFuture;
import static com.spotify.trickle.Trickle.call;

/**
 * Integration-level Trickle tests.
 */
public class TrickleApiTest {
  public static final String DUPLICATE_BINDING_FOR_INPUT = "Duplicate binding for input";
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void shouldThrowForMissingInput() throws Exception {
    Func1<String, String> node1 = new Func1<String, String>() {
      @Override
      public ListenableFuture<String> run(String arg) {
        return immediateFuture(arg + ", 1");
      }
    };

    Input<String> input = Input.named("somethingWeirdd");

    Graph<String> g = call(node1).with(input);

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Input not bound to a value");
    thrown.expectMessage("somethingWeirdd");

    g.run();
  }

  @Test
  public void shouldThrowForDuplicateBindOfName() throws Exception {
    Func1<String, String> node1 = new Func1<String, String>() {
      @Override
      public ListenableFuture<String> run(String arg) {
        return immediateFuture(arg + ", 1");
      }
    };

    Input<String> input = Input.named("mein Name");

    Graph<String> g = call(node1).with(input);

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage(DUPLICATE_BINDING_FOR_INPUT);
    thrown.expectMessage("mein Name");

    g.bind(input, "erich").bind(input, "volker");
  }

  @Test
  public void shouldThrowForDuplicateBindOfNameInChainedSubgraphs() throws Exception {
    Func1<String, String> node1 = new Func1<String, String>() {
      @Override
      public ListenableFuture<String> run(String arg) {
        return immediateFuture(arg + ", 1");
      }
    };

    Input<String> input = Input.named("mein Name");

    Graph<String> g1 = call(node1).with(input).bind(input, "erich");
    Graph<String> g2 = call(node1).with(g1);

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage(DUPLICATE_BINDING_FOR_INPUT);
    thrown.expectMessage("mein Name");

    g2.bind(input, "volker").run();
  }

  @Test
  public void shouldThrowForDuplicateBindOfNameInDiamondSubgraphs() throws Exception {
    Func1<String, String> node1 = new Func1<String, String>() {
      @Override
      public ListenableFuture<String> run(String arg) {
        return immediateFuture(arg + ", 1");
      }
    };
   Func2<String, String, String> node2 = new Func2<String, String, String>() {
      @Override
      public ListenableFuture<String> run(String arg, String arg2) {
        return immediateFuture(arg + ", " + arg2);
      }
    };

    Input<String> input = Input.named("mitt namn");
    Input<String> input2 = Input.named("nåt");

    Graph<String> g1 = call(node1).with(input).bind(input, "erik").bind(input2, "hej");
    Graph<String> g2 = call(node1).with(input).bind(input, "folke").bind(input2, "hopp");

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage(DUPLICATE_BINDING_FOR_INPUT);
    thrown.expectMessage("mitt namn");
    thrown.expectMessage("nåt");

    // creating the 'bad' graph after setting up the thrown expectations, since it would be nice
    // to be able to detect the problem at construction time rather than runtime.
    Graph<String> g3 = call(node2).with(g1, g2);
    g3.run();
  }
}
