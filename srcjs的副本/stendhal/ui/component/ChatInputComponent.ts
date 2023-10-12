/***************************************************************************
 *                (C) Copyright 2003-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare let marauroa: any;
declare let stendhal: any;

import { KeyHandler } from "../KeyHandler";
import { Component } from "../toolkit/Component";
import { singletons } from "../../SingletonRepo";


const config = singletons.getConfigManager();
const slashActions = singletons.getSlashActionRepo();

/**
 * chat input text field
 */
export class ChatInputComponent extends Component {

	private history: string[];
	private historyIndex: number;
	private inputElement: HTMLInputElement;

	constructor() {
		super("chatinput");
		this.inputElement = this.componentElement as HTMLInputElement;
		this.componentElement.addEventListener("keydown", (event: KeyboardEvent) => {
			this.onKeyDown(event);
		});
		this.componentElement.addEventListener("keypress", (event: KeyboardEvent) => {
			this.onKeyPress(event);
		});
		// restore from previous session
		this.history = config.getObject("chat.history") || [];
		this.historyIndex = config.getInt("chat.history.index", 0);
	}

	public clear() {
		this.inputElement.value = "";
	}

	public setText(text: string) {
		this.inputElement.value = text;
		this.inputElement.focus();
	}

	private fromHistory(i: number) {
		this.historyIndex = this.historyIndex + i;
		if (this.historyIndex < 0) {
			this.historyIndex = 0;
		}
		if (this.historyIndex >= this.history.length) {
			this.historyIndex = this.history.length;
			this.clear();
		} else {
			this.inputElement.value = this.history[this.historyIndex];
		}
	}

	onKeyDown(event: KeyboardEvent) {
		let code = stendhal.ui.html.extractKeyCode(event);

		if (event.shiftKey) {
			// chat history
			if (code === KeyHandler.keycode.up) {
				event.preventDefault();
				this.fromHistory(-1);
			} else if (code === KeyHandler.keycode.down){
				event.preventDefault();
				this.fromHistory(1);
			}
		}
	}

	private onKeyPress(event: KeyboardEvent) {
		if (event.keyCode === 13) {
			this.send();
			event.preventDefault();
		}
	}

	private remember(text: string) {
		if (this.history.length > 100) {
			this.history.shift();
		}
		this.history[this.history.length] = text;
		this.historyIndex = this.history.length;
		// preserve across sessions
		// XXX: should this be done at logout/destruction for performance?
		config.set("chat.history", this.history);
		config.set("chat.history.index", this.historyIndex);
	}

	private send() {
		let val = this.inputElement.value;
		let array = val.split(" ");
		if (array[0] === "/choosecharacter") {
			marauroa.clientFramework.chooseCharacter(array[1]);
		} else if (val === '/close') {
			marauroa.clientFramework.close();
		} else {
			if (slashActions.execute(val)) {
				this.remember(val);
			}
		}
		this.clear();
	}

}
