from flask import Blueprint, jsonify, request
from services.fishing_detector import FishingDetector

api_bp = Blueprint('api', __name__)
fishing_detector = FishingDetector()

@api_bp.route("/api/hello")
def hello():
    return jsonify({"message": "Hello from Python backend!"})

@api_bp.route("/api/goodbye")
def goodbye():
    return jsonify({"message": "Goodbye from Python backend!"})

@api_bp.route("/")
def analyze_sample_email():
    """Analyze a sample email for fishing detection"""
    try:
        result = fishing_detector.get_sample_analysis()
        return jsonify(result)
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@api_bp.route("/analyze", methods=["POST"])
def analyze_email():
    """Analyze provided email content for fishing detection"""
    try:
        # Verifica se é JSON
        if request.is_json:
            data = request.get_json()
            if not data or 'email_content' not in data:
                return jsonify({"error": "email_content is required"}), 400
            email_content = data['email_content']
        else:
            # Aceita texto cru
            email_content = request.get_data(as_text=True)
            if not email_content.strip():
                return jsonify({"error": "Email content cannot be empty"}), 400
        
        result = fishing_detector.analyze_email(email_content)
        return jsonify(result)
    except Exception as e:
        return jsonify({"error": str(e)}), 500