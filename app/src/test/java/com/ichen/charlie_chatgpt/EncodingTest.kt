package com.ichen.charlie_chatgpt

import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.Encoding
import com.knuddels.jtokkit.api.EncodingRegistry
import com.knuddels.jtokkit.api.ModelType
import org.junit.Test

import org.junit.Assert.*

/*
    Assert that local encoding/decoding matches the OpenAI online tokenizer
    https://platform.openai.com/tokenizer
 */
class EncodingTest {

    var registry: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
    var encoding: Encoding = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO)

    @Test
    fun `verify encoded length is accurate for simple string`() {
        val encoded = encoding.encode(
            "Hello world"
        )
        assertEquals(2, encoded.size)
    }

    @Test
    fun `verify encoded length is accurate for complex string`() {
        val encoded = encoding.encode(
            "I was taking a walk the other day, when I saw a blind woman."
        )
        assertEquals(16, encoded.size)
    }
}