from flask import Flask
from routes.routes_api import api_bp

def create_app():
    app = Flask(__name__)
    
    # Register blueprints
    app.register_blueprint(api_bp)
    
    return app

if __name__ == "__main__":
    app = create_app()
    app.run(host="0.0.0.0", port=5000, debug=True)