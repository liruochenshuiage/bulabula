/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

public class CreateChallengeAction implements SlashAction {

	@Override
	public boolean execute(String[] params, String remainder) {
		if(params == null) {
			return false;
		}
		RPAction action = new RPAction();
		action.put("type", "challenge");
		action.put("action", "open");
		action.put("target", params[0]);
		ClientSingletonRepository.getClientFramework().send(action);

		return true;
	}

	@Override
	public int getMaximumParameters() {
		return 1;
	}

	@Override
	public int getMinimumParameters() {
		return 1;
	}

}
