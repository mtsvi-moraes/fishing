from config.settings import Config
from utils.response_parser import parse_gemini_response
from typing import Dict, Any

class FishingDetector:
    def __init__(self):
        self.config = Config()
    
    def analyze_email(self, email_content: str) -> Dict[Any, Any]:
        """Analyze email content for fishing detection"""
        try:
            response = self.config.generate_content(email_content)
            return parse_gemini_response(response)
        except Exception as e:
            # Log error in production
            raise RuntimeError(f"Fishing analysis failed: {e}")
    
    def get_sample_analysis(self) -> Dict[Any, Any]:
        """Get analysis of a sample suspicious email"""
        sample_email = """
Assunto: 🚨 Alerta de Segurança: Acesso suspeito à sua conta bancária

De: suporte@itauseguro-online.com.br
Para: cliente@exemplo.com

Prezado(a) cliente,

Detectamos uma atividade suspeita em sua conta na madrugada de hoje (11/06/2025), às 03:14 AM, a partir de um dispositivo desconhecido localizado em Fortaleza/CE.

Por motivos de segurança, sua conta foi temporariamente bloqueada até que possamos verificar sua identidade.

Para desbloquear sua conta e evitar a suspensão permanente, pedimos que acesse o link abaixo e confirme suas informações:

🔒 Acesse sua conta com segurança

Caso o procedimento não seja realizado nas próximas 12 horas, sua conta será suspensa automaticamente como medida preventiva.

Agradecemos sua compreensão.
Atenciosamente,
Equipe de Segurança Itaú Unibanco
suporte@itauseguro-online.com.br

Este é um e-mail automático. Não responda a esta mensagem.
"""
        return self.analyze_email(sample_email)