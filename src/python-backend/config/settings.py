import os
from dotenv import load_dotenv
from google.genai import Client

# Carrega variáveis do .env
load_dotenv()

class Config:
    def __init__(self):
        self.GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
        self.GEMINI_MODEL = os.getenv("GEMINI_MODEL", "gemini-2.0-flash")  # valor padrão
        
        if not self.GEMINI_API_KEY:
            raise ValueError("GEMINI_API_KEY deve estar definido no .env")
        if not self.GEMINI_MODEL:
            raise ValueError("GEMINI_MODEL deve estar definido no .env")

        self.client = Client(api_key=self.GEMINI_API_KEY)

    def generate_content(self, prompt: str) -> str:
        """Gera conteúdo usando o modelo Gemini"""
        system_prompt = self._get_system_prompt()
        full_prompt = f"{system_prompt}\n\nEmail:\n{prompt}"
        
        response = self.client.models.generate_content(
            model=self.GEMINI_MODEL,
            contents=full_prompt,
        )
        return response.text
    
    def _get_system_prompt(self) -> str:
        """Retorna o prompt do sistema para análise de spam"""
        return """
You are required to read from an e-mail received from a user and detect whether the
corpus of the e-mail is a spam or not. Your output must be a JSON object with the following structure:
{
    "subject": "subject of the e-mail in a few words being a string",
    "is_spam": true or false,
    "email_content": "the content of the e-mail being a string",
    "confidence": a number between 0 and 1 representing the confidence of the spam detection,
    "time_detected": "time in Brazilian time when the spam detection was made"
} 
Your output must be a valid JSON object and nothing else (No text, no markdown, no code block).
"""
