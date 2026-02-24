package org.ecnumc.voxelflow.test;

import org.ecnumc.voxelflow.converter.CamelToSnakeConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CamelToSnakeConverter 单元测试喵~
 *
 * @author liudongyu
 */
@SpringBootTest
class CamelToSnakeConverterTest {
	@Autowired
	private CamelToSnakeConverter converter;

	/**
	 * 测试 null 输入喵~
	 */
	@Test
	void testConvertNull() {
		assertNull(this.converter.convert(null));
	}

	/**
	 * 测试空字符串输入喵~
	 */
	@Test
	void testConvertEmpty() {
		assertEquals("", this.converter.convert(""));
	}

	/**
	 * 测试普通驼峰命名（小写开头）喵~
	 */
	@Test
	void testConvertCamelCase() {
		assertEquals("create_at", this.converter.convert("createAt"));
		assertEquals("user_id", this.converter.convert("userId"));
		assertEquals("get_full_name", this.converter.convert("getFullName"));
		assertEquals("is_active", this.converter.convert("isActive"));
	}

	/**
	 * 测试 Pascal 命名（大写开头）喵~
	 */
	@Test
	void testConvertPascalCase() {
		assertEquals("user_id", this.converter.convert("UserId"));
		assertEquals("create_at", this.converter.convert("CreateAt"));
		assertEquals("base_response", this.converter.convert("BaseResponse"));
	}

	/**
	 * 测试连续大写字母喵~
	 */
	@Test
	void testConvertConsecutiveUpperCase() {
		assertEquals("https_connection", this.converter.convert("HTTPSConnection"));
		assertEquals("http_response", this.converter.convert("HTTPResponse"));
		assertEquals("xml_parser", this.converter.convert("XMLParser"));
		assertEquals("json_data", this.converter.convert("JSONData"));
	}

	/**
	 * 测试全大写字符串喵~
	 */
	@Test
	void testConvertAllUpperCase() {
		assertEquals("id", this.converter.convert("ID"));
		assertEquals("http", this.converter.convert("HTTP"));
		assertEquals("url", this.converter.convert("URL"));
	}

	/**
	 * 测试全小写字符串喵~
	 */
	@Test
	void testConvertAllLowerCase() {
		assertEquals("name", this.converter.convert("name"));
		assertEquals("description", this.converter.convert("description"));
	}

	/**
	 * 测试单个字符喵~
	 */
	@Test
	void testConvertSingleCharacter() {
		assertEquals("a", this.converter.convert("a"));
		assertEquals("a", this.converter.convert("A"));
	}

	/**
	 * 测试两个字符喵~
	 */
	@Test
	void testConvertTwoCharacters() {
		assertEquals("ab", this.converter.convert("ab"));
		assertEquals("ab", this.converter.convert("Ab"));
		assertEquals("ab", this.converter.convert("AB"));
	}

	/**
	 * 测试以大写开头的单词喵~
	 */
	@Test
	void testConvertUpperCaseStart() {
		assertEquals("user", this.converter.convert("User"));
		assertEquals("name", this.converter.convert("Name"));
	}

	/**
	 * 测试带数字的命名喵~
	 */
	@Test
	void testConvertWithNumbers() {
		assertEquals("user_id2", this.converter.convert("userId2"));
		assertEquals("version1_name", this.converter.convert("version1Name"));
	}

	/**
	 * 测试复杂场景：多个连续大写字母后跟驼峰喵~
	 */
	@Test
	void testConvertComplexScenario() {
		assertEquals("httpsurl_connection", this.converter.convert("HTTPSURLConnection"));
		assertEquals("xml_http_request", this.converter.convert("XMLHttpRequest"));
		assertEquals("io_exception", this.converter.convert("IOException"));
	}

	/**
	 * 测试已经是蛇形命名的字符串喵~
	 */
	@Test
	void testConvertAlreadySnakeCase() {
		assertEquals("user_id", this.converter.convert("user_id"));
		assertEquals("create_at", this.converter.convert("create_at"));
	}
}