import json
from typing import Dict, Any

def parse_gemini_response(response_str: str) -> Dict[Any, Any]:
    """Parse response from Gemini API, removing markdown formatting"""
    try:
        # Remove crases e 'json' do início/fim
        cleaned = response_str.strip().removeprefix("```json").removesuffix("```").strip()
        return json.loads(cleaned)
    except json.JSONDecodeError as e:
        raise ValueError(f"Failed to parse Gemini response: {e}")