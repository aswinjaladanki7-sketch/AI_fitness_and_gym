"""
============================================================
AI GYM & FITNESS ASSISTANT - Backend REST Server
SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
School of Computer Science and Engineering
Department of Computer Science and Engineering
Course: 21CSC203P - PROJECT
============================================================
"""

import os
from pathlib import Path
from flask import Flask, jsonify, send_from_directory
from flask_cors import CORS
from dotenv import load_dotenv

# Load environment
base_dir = Path(__file__).resolve().parent.parent
load_dotenv(base_dir / ".env")

# Import database initializer
from .database.db import init_db

# Import blueprints
from .routes.auth import auth_bp
from .routes.user import user_bp
from .routes.fitness import fitness_bp
from .routes.nutrition import nutrition_bp
from .routes.workout import workout_bp
from .routes.ai import ai_bp
from .routes.progress import progress_bp

def create_app():
    app = Flask(__name__, static_folder="../frontend")
    CORS(app, resources={r"/api/*": {"origins": "*"}})

    app.config['SECRET_KEY'] = os.getenv("SECRET_KEY", "ai-gym-fitness-secret-key-srmist-21csc203p")
    app.config['JSON_SORT_KEYS'] = False

    # Initialize Database
    try:
        init_db()
    except Exception as e:
        print(f"[!] Database initialization warning: {e}")

    # Register Blueprints under /api
    app.register_blueprint(auth_bp, url_prefix='/api/auth')
    app.register_blueprint(user_bp, url_prefix='/api/user')
    app.register_blueprint(fitness_bp, url_prefix='/api/fitness')
    app.register_blueprint(nutrition_bp, url_prefix='/api')
    app.register_blueprint(workout_bp, url_prefix='/api')
    app.register_blueprint(ai_bp, url_prefix='/api')
    app.register_blueprint(progress_bp, url_prefix='/api')

    @app.route('/api/health', methods=['GET'])
    def health_check():
        return jsonify({
            "status": "online",
            "project": "AI GYM & FITNESS ASSISTANT",
            "institution": "SRM INSTITUTE OF SCIENCE AND TECHNOLOGY",
            "course": "21CSC203P - PROJECT",
            "tagline": "Train Smarter. Eat Better. Move Better."
        })

    # Serve frontend files if accessed through backend server
    @app.route('/')
    def serve_index():
        return send_from_directory(app.static_folder, 'index.html')

    @app.route('/<path:path>')
    def serve_static(path):
        target = Path(app.static_folder) / path
        if target.exists():
            return send_from_directory(app.static_folder, path)
        return send_from_directory(app.static_folder, 'index.html')

    return app

app = create_app()

if __name__ == '__main__':
    port = int(os.getenv("PORT", 5000))
    print(f"\n=======================================================")
    print(f"  AI GYM & FITNESS ASSISTANT - REST API SERVER")
    print(f"  SRM IST | 21CSC203P - PROJECT")
    print(f"  Server listening on: http://localhost:{port}")
    print(f"=======================================================\n")
    app.run(host='0.0.0.0', port=port, debug=True)
