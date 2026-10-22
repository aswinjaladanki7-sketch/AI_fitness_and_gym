"""
AI GYM & FITNESS - Form Analyzer & Real-Time Biomechanical Coach
Evaluates kinematic angles against athletic standards, calculates 0-100 form score,
and generates context-aware correction feedback.
"""

def analyze_form(exercise_name: str, angles: dict, state: str = 'INITIAL'):
    """
    Analyzes biomechanical parameters for current exercise frame.
    Returns:
      {
         'score': 0-100,
         'category': 'Excellent' | 'Good' | 'Needs Improvement' | 'Poor',
         'status': 'correct' | 'warning' | 'error',
         'feedback': '...',
         'cues': [...]
      }
    """
    ex = exercise_name.lower().replace("_", "-")
    primary = angles.get('primary_angle', 180.0)
    secondary = angles.get('secondary_angle', 180.0)

    score = 95.0
    feedback = "Good form"
    status = "correct"
    cues = []

    # 1. SQUATS
    if 'squat' in ex:
        hip_angle = angles.get('hip_angle', 170.0)

        if state == 'BOTTOM':
            if primary > 105:
                score -= 25
                feedback = "Go lower (thighs parallel)"
                status = "warning"
                cues.append("Reach at least 90 degree knee flexion")
            elif primary < 60:
                score -= 10
                feedback = "Don't bottom out too deep"
                status = "warning"
            else:
                score = 98
                feedback = "Perfect depth! Drive up"
                status = "correct"

        if hip_angle < 70 and state in ('DESCENDING', 'BOTTOM'):
            score -= 20
            feedback = "Keep your back straight"
            status = "error"
            cues.append("Engage core, lift chest")

        if state == 'ASCENDING' and score >= 80:
            feedback = "Control your movement"

    # 2. PUSH-UPS
    elif 'push' in ex:
        body_line = angles.get('body_line_angle', 180.0)

        if abs(body_line - 180.0) > 25.0:
            score -= 25
            feedback = "Keep your body straight"
            status = "error"
            cues.append("Do not let hips sag or pike upward")

        if state == 'BOTTOM':
            if primary > 100:
                score -= 20
                feedback = "Lower your chest further"
                status = "warning"
                cues.append("Lower until elbows reach ~90 degrees")
            else:
                feedback = "Great depth! Push hard"
                status = "correct"

        if state == 'ASCENDING' and status != 'error':
            feedback = "Keep your elbows aligned"

    # 3. LUNGES
    elif 'lunge' in ex:
        if state == 'BOTTOM':
            if primary > 105:
                score -= 20
                feedback = "Lower your body further"
                status = "warning"
            elif primary < 75:
                score -= 15
                feedback = "Keep your front knee aligned"
                status = "warning"
                cues.append("Knee directly over ankle, not past toes")
            else:
                feedback = "Excellent stance and depth"
                status = "correct"
        elif state == 'DESCENDING':
            feedback = "Maintain balance"

    # 4. BICEP CURLS
    elif 'curl' in ex:
        shoulder_stability = angles.get('shoulder_stability', 20.0)

        if shoulder_stability > 45.0:
            score -= 25
            feedback = "Keep your elbow stable"
            status = "error"
            cues.append("Pin elbows to sides, stop swinging")

        if state == 'PEAK':
            if primary > 65:
                score -= 15
                feedback = "Complete the curl"
                status = "warning"
            else:
                feedback = "Great peak contraction!"
                status = "correct"
        elif state == 'LOWERING':
            feedback = "Control the downward movement"

    # 5. SHOULDER PRESS
    elif 'shoulder' in ex or 'press' in ex:
        if state == 'RACK':
            feedback = "Press upward"
        elif state == 'OVERHEAD':
            if primary < 145:
                score -= 20
                feedback = "Maintain proper alignment"
                status = "warning"
                cues.append("Extend arms fully overhead without locking")
            else:
                feedback = "Strong overhead lockout!"
                status = "correct"
        elif state == 'LOWERING':
            feedback = "Keep your movement controlled"

    # Clamp score
    score = max(40.0, min(100.0, round(score, 1)))

    # Determine category according to rubric:
    # 90-100: Excellent Form, 75-89: Good Form, 60-74: Needs Improvement, Below 60: Poor Form
    if score >= 90:
        category = "Excellent Form"
    elif score >= 75:
        category = "Good Form"
    elif score >= 60:
        category = "Needs Improvement"
    else:
        category = "Poor Form"

    return {
        "score": score,
        "category": category,
        "status": status,
        "feedback": feedback,
        "cues": cues
    }
