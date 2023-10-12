/***************************************************************************
 *                    Copyright © 2020-2023 - Stendhal                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting.lua;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;


/**
 * Adds some useful function members to Lua "string" table.
 */
public class LuaStringHelper {

	private static final LuaLogger logger = LuaLogger.get();

	/** The singleton instance. */
	private static LuaStringHelper instance;


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 *   Static LuaStringHelper instance.
	 */
	public static LuaStringHelper get() {
		if (instance == null) {
			instance = new LuaStringHelper();
		}
		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private LuaStringHelper() {
		// singleton
	}

	/**
	 * Adds custom functions to the Lua "string" table.
	 *
	 * @param stringTable
	 *   Lua "string" table.
	 */
	public void init(final LuaTable stringTable) {

		// add string.builder method
		stringTable.set("builder", new LuaFunction() {

			/**
			 *
			 * string.builder
			 *
			 * Creates a `StringBuilder` for use in Lua.
			 *
			 * @return
			 *   New `StringBuilder` instance.
			 */
			@Override
			public LuaUserdata call() {
				return (LuaUserdata) CoerceJavaToLua.coerce(new StringBuilder());
			}

			/**
			 * string.builder
			 *
			 * Creates a `StringBuilder` for use in Lua.
			 *
			 * @param arg
			 *   Lua string to initialize `StringBuilder` with or `LuaNil`.
			 * @return
			 *   New `StringBuilder` instance.
			 */
			@Override
			public LuaUserdata call(final LuaValue arg) {
				if (arg.isnil()) {
					logger.warn("Argument of string.builder is nil");
					return call();
				}
				return (LuaUserdata) CoerceJavaToLua.coerce(new StringBuilder(arg.tojstring()));
			}
		});

		// add string.split method
		stringTable.set("split", new LuaFunction() {

			/**
			 * string.split
			 *
			 * @return
			 *   List of strings.
			 * @see
			 *   LuaStringHelper.split
			 */
			@Override
			public LuaValue call(final LuaValue arg1, final LuaValue arg2) {
				return split(arg1.strvalue(), arg2.strvalue());
			}
		});

		// add string.isNumber method
		stringTable.set("isNumber", new LuaFunction() {

			/**
			 * string.isNumber
			 *
			 * Checks if a string contains numeric characters only.
			 *
			 * @param arg
			 *   The Lua string to be checked.
			 * @return
			 *   `LuaBoolean.TRUE` if all characters are numeric, `LuaBoolean.FALSE` otherwise.
			 */
			@Override
			public LuaBoolean call(final LuaValue arg) {
				final String st = arg.tojstring();
				try {
					Integer.parseInt(st);
				} catch (final NumberFormatException e) {
					return LuaBoolean.FALSE;
				}
				return LuaBoolean.TRUE;
			}
		});

		// add string.startsWith method
		stringTable.set("startsWith", new LuaFunction() {

			/**
			 * strings.startsWith
			 *
			 * Checks if a string begins with a specified set of characters.
			 *
			 * @param orig
			 *   The original Lua string to be checked.
			 * @param prefix
			 *   Prefix to compare against.
			 * @return
			 *   `LuaBoolean.TRUE` if the characters in prefix match the beginning of orig,
			 *   `LuaBoolean.FALSE` otherwise.
			 */
			@Override
			public LuaBoolean call(final LuaValue orig, final LuaValue prefix) {
				return (LuaBoolean) CoerceJavaToLua.coerce(orig.tojstring().startsWith(prefix.tojstring()));
			}
		});

		// add string.endsWith method
		stringTable.set("endsWith", new LuaFunction() {

			/**
			 * string.endsWith
			 *
			 * Checks if a string ends with a specified set of characters.
			 *
			 * @param orig
			 *   The original Lua string to be checked.
			 * @param suffix
			 *   Suffix to compare against.
			 * @return
			 *   `LuaBoolean.TRUE` if the characters in suffix match the end of orig, `LuaBoolean.FALSE`
			 *    otherwise.
			 */
			@Override
			public LuaBoolean call(final LuaValue orig, final LuaValue suffix) {
				return (LuaBoolean) CoerceJavaToLua.coerce(orig.tojstring().endsWith(suffix.tojstring()));
			}
		});

		// add string.valueOf method
		stringTable.set("valueOf", new LuaFunction() {
			/**
			 * string.valueOf
			 *
			 * Retrieves string value of an object.
			 *
			 * @param obj
			 *   Object instance to be converted.
			 * @return
			 *   String value of object.
			 */
			@Override
			public LuaString call(final LuaValue obj) {
				return (LuaString) CoerceJavaToLua.coerce(String.valueOf(obj));
			}
		});
	}

	/**
	 * Splits a string into a Lua table.
	 *
	 * @param str
	 *   The string to be split.
	 * @param delim
	 *   The delimiter character(s) used to split the string.
	 * @return
	 *   Lua table containing string elements.
	 */
	private LuaTable split(final LuaString str, final LuaString delim) {
		final LuaTable table = new LuaTable();
		final String[] tmpArray = str.tojstring().split(delim.tojstring());
		for (final String st: tmpArray) {
			table.insert(table.length() + 1, LuaValue.valueOf(st));
		}
		return table;
	}
}
