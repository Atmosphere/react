/*
 * Copyright 2013 Donghwan Kim
 *
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
 */
package io.github.flowersinthesand.wes.support;

import io.github.flowersinthesand.wes.Actions;
import io.github.flowersinthesand.wes.Actions.Options;
import io.github.flowersinthesand.wes.ActionsTestTemplate;

public class ConcurrentActionsTest extends ActionsTestTemplate {

	@Override
	protected <T> Actions<T> createActions() {
		return new ConcurrentActions<>();
	}

	@Override
	protected <T> Actions<T> createActions(Options options) {
		return new ConcurrentActions<>(options);
	}

}
