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

import com.google.common.collect.Lists;
import com.google.common.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BindingDepTest {

  Input<String> input;
  BindingDep<String> dep;

  @Before
  public void setUp() throws Exception {
    input = Input.named("ho");
    dep = new BindingDep<String>(input);
  }

  @Test
  public void shouldGenerateEqualNodeInfoForSameName() throws Exception {
    // this is important for understanding how nodes relate to each other when inspecting a call
    // graph - if the same input name is used in more than one place, different BindingDep instances
    // will be created, but they should be considered equal since they refer to the same input.

    BindingDep<String> dep1 = new BindingDep<String>(Input.<String>named("hi"));
    BindingDep<String> dep2 = new BindingDep<String>(Input.<String>named("hi"));

    new EqualsTester()
        .addEqualityGroup(dep1.getNodeInfo(), dep2.getNodeInfo())
        .addEqualityGroup(dep.getNodeInfo())
        .testEquals();
  }

  @Test
  public void shouldUseInputNameInNodeInfo() throws Exception {
    assertThat(dep.getNodeInfo().name(), equalTo(input.getName()));
  }

  @Test
  public void shouldReturnNodeInfoWithoutPredecessors() throws Exception {
    Iterable<? extends NodeInfo> predecessors = dep.getNodeInfo().predecessors();

    assertThat(Lists.newArrayList(predecessors).isEmpty(), is(true));
  }

  @Test
  public void shouldReturnNodeInfoWithoutArguments() throws Exception {
    assertThat(dep.getNodeInfo().arguments().isEmpty(), is(true));
  }

  @Test
  public void shouldReturnNodeInfoWithTypeInput() throws Exception {
    assertThat(dep.getNodeInfo().type(), equalTo(NodeInfo.Type.INPUT));
  }
}
