package cn.hutool.script;

import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.util.StrUtil;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 脚本工具类
 *
 * @author Looly
 */
public class ScriptUtil {

	private static final ScriptEngineManager manager = new ScriptEngineManager();
	private static SimpleCache<String, ScriptEngine> cache = new SimpleCache<>();

	/**
	 * 获得单例的{@link ScriptEngine} 实例
	 *
	 * @param nameOrExtOrMime 脚本名称
	 * @return {@link ScriptEngine} 实例
	 */
	public static ScriptEngine getScript(String nameOrExtOrMime) {
		return cache.get(nameOrExtOrMime, ()-> createScript(nameOrExtOrMime));
	}

	/**
	 * 创建 {@link ScriptEngine} 实例
	 *
	 * @param nameOrExtOrMime 脚本名称
	 * @return {@link ScriptEngine} 实例
	 * @since 5.2.6
	 */
	public static ScriptEngine createScript(String nameOrExtOrMime) {
		ScriptEngine engine = manager.getEngineByName(nameOrExtOrMime);
		if (null == engine) {
			engine = manager.getEngineByExtension(nameOrExtOrMime);
		}
		if (null == engine) {
			engine = manager.getEngineByMimeType(nameOrExtOrMime);
		}
		if (null == engine) {
			throw new NullPointerException(StrUtil.format("Script for [{}] not support !", nameOrExtOrMime));
		}
		return engine;
	}

	/**
	 * 获得非单例的 Javascript引擎 {@link JavaScriptEngine}
	 *
	 * @return {@link JavaScriptEngine}
	 */
	public static JavaScriptEngine getJavaScriptEngine() {
		return new JavaScriptEngine();
	}

	/**
	 * 获得单例的JavaScript引擎
	 *
	 * @return Javascript引擎
	 * @since 5.2.5
	 */
	public static ScriptEngine getJsEngine() {
		return getScript("js");
	}

	/**
	 * 创建新的JavaScript引擎
	 *
	 * @return Javascript引擎
	 * @since 5.2.6
	 */
	public static ScriptEngine createJsEngine() {
		return createScript("js");
	}

	/**
	 * 获得单例的Python引擎<br>
	 * 需要引入org.python:jython
	 *
	 * @return Python引擎
	 * @since 5.2.5
	 */
	public static ScriptEngine getPythonEngine() {
		System.setProperty("python.import.site", "false");
		return getScript("python");
	}

	/**
	 * 创建Python引擎<br>
	 * 需要引入org.python:jython
	 *
	 * @return Python引擎
	 * @since 5.2.6
	 */
	public static ScriptEngine createPythonEngine() {
		System.setProperty("python.import.site", "false");
		return createScript("python");
	}

	/**
	 * 获得单例的Lua引擎<br>
	 * 需要引入org.luaj:luaj-jse
	 *
	 * @return Lua引擎
	 * @since 5.2.5
	 */
	public static ScriptEngine getLuaEngine() {
		return getScript("lua");
	}

	/**
	 * 创建Lua引擎<br>
	 * 需要引入org.luaj:luaj-jse
	 *
	 * @return Lua引擎
	 * @since 5.2.6
	 */
	public static ScriptEngine createLuaEngine() {
		return createScript("lua");
	}

	/**
	 * 获得单例的Groovy引擎<br>
	 * 需要引入org.codehaus.groovy:groovy-all
	 *
	 * @return Groovy引擎
	 * @since 5.2.5
	 */
	public static ScriptEngine getGroovyEngine() {
		return getScript("groovy");
	}

	/**
	 * 创建Groovy引擎<br>
	 * 需要引入org.codehaus.groovy:groovy-all
	 *
	 * @return Groovy引擎
	 * @since 5.2.6
	 */
	public static ScriptEngine createGroovyEngine() {
		return createScript("groovy");
	}

	/**
	 * 执行Javascript脚本
	 *
	 * @param script 脚本内容
	 * @return 执行结果
	 * @throws ScriptRuntimeException 脚本异常
	 * @since 3.2.0
	 */
	public static Object eval(String script) throws ScriptRuntimeException {
		try {
			return getJsEngine().eval(script);
		} catch (ScriptException e) {
			throw new ScriptRuntimeException(e);
		}
	}

	/**
	 * 执行脚本
	 *
	 * @param script  脚本内容
	 * @param context 脚本上下文
	 * @return 执行结果
	 * @throws ScriptRuntimeException 脚本异常
	 * @since 3.2.0
	 */
	public static Object eval(String script, ScriptContext context) throws ScriptRuntimeException {
		try {
			return getJsEngine().eval(script, context);
		} catch (ScriptException e) {
			throw new ScriptRuntimeException(e);
		}
	}

	/**
	 * 执行脚本
	 *
	 * @param script   脚本内容
	 * @param bindings 绑定的参数
	 * @return 执行结果
	 * @throws ScriptRuntimeException 脚本异常
	 * @since 3.2.0
	 */
	public static Object eval(String script, Bindings bindings) throws ScriptRuntimeException {
		try {
			return getJsEngine().eval(script, bindings);
		} catch (ScriptException e) {
			throw new ScriptRuntimeException(e);
		}
	}

	/**
	 * 编译Javascript脚本
	 *
	 * @param script 脚本内容
	 * @return {@link CompiledScript}
	 * @throws ScriptRuntimeException 脚本异常
	 * @since 3.2.0
	 */
	public static CompiledScript compile(String script) throws ScriptRuntimeException {
		try {
			return compile(getJsEngine(), script);
		} catch (ScriptException e) {
			throw new ScriptRuntimeException(e);
		}
	}

	/**
	 * 编译Javascript脚本
	 *
	 * @param engine 引擎
	 * @param script 脚本内容
	 * @return {@link CompiledScript}
	 * @throws ScriptException 脚本异常
	 */
	public static CompiledScript compile(ScriptEngine engine, String script) throws ScriptException {
		if (engine instanceof Compilable) {
			final Compilable compEngine = (Compilable) engine;
			return compEngine.compile(script);
		}
		return null;
	}
}
