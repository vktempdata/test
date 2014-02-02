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

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TrickleNodeTest {
  Object result;
  ListenableFuture<Object> future;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    result = new Object();
    future = Futures.immediateFuture(result);
  }

  @Test
  public void shouldInstantiateNode0() throws Exception {
    TrickleNode node = TrickleNode.create(new Func0<Object>() {
      @Override
      public ListenableFuture<Object> run() {
        return future;
      }
    });

    assertThat(node.run(ImmutableList.of()).get(), equalTo(result));
  }

  @Test
  public void shouldInstantiateNode1() throws Exception {
    TrickleNode node = TrickleNode.create(new Func1<String, Object>() {
      @Override
      public ListenableFuture<Object> run(String arg) {
        return future;
      }
    });

    assertThat(node.run(ImmutableList.<Object>of("hi")).get(), equalTo(result));
  }

  @Test
  public void shouldInstantiateNode2() throws Exception {
    TrickleNode node = TrickleNode.create(new Func2<String, String, Object>() {
      @Override
      public ListenableFuture<Object> run(String arg, String arg2) {
        return future;
      }
    });

    assertThat(node.run(ImmutableList.<Object>of("hi", "there")).get(), equalTo(result));
  }

  @Test
  public void shouldInstantiateNode3() throws Exception {
    TrickleNode node = TrickleNode.create(new Func3<String, String, String, Object>() {
      @Override
      public ListenableFuture<Object> run(String arg, String arg2, String arg3) {
        return future;
      }
    });

    assertThat(node.run(ImmutableList.<Object>of("hi", "there", "you")).get(), equalTo(result));
  }

  @Test
  public void shouldFailForUnknownNode() throws Exception {
    thrown.expect(IllegalArgumentException.class);

    TrickleNode.create(new Func<Object>() { });
  }
}
