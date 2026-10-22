"""
AI GYM & FITNESS - Exercise & Joint Angle Detection
Computes biomechanical vectors and joint angles between kinematic landmarks.
"""

import math

def calculate_angle(point_a, point_b, point_c):
    """
    Calculates the 2D joint angle at vertex B (in degrees 0-180)
    between points A, B, and C.
    """
    if not point_a or not point_b or not point_c:
        return 0.0

    ax, ay = point_a['x'], point_a['y']
    bx, by = point_b['x'], point_b['y']
    cx, cy = point_c['x'], point_c['y']

    # Vector BA and Vector BC
    ba_x = ax - bx
    ba_y = ay - by
    bc_x = cx - bx
    bc_y = cy - by

    # Dot product and magnitudes
    dot_product = (ba_x * bc_x) + (ba_y * bc_y)
    mag_ba = math.hypot(ba_x, ba_y)
    mag_bc = math.hypot(bc_x, bc_y)

    if mag_ba == 0 or mag_bc == 0:
        return 0.0

    cosine = dot_product / (mag_ba * mag_bc)
    # Clamp cosine between -1.0 and 1.0 to avoid domain errors
    cosine = max(-1.0, min(1.0, cosine))
    angle_rad = math.acos(cosine)
    angle_deg = math.degrees(angle_rad)

    return round(angle_deg, 1)

def extract_exercise_angles(exercise_name: str, landmarks):
    """
    Extracts relevant kinetic joint angles depending on selected exercise.
    """
    ex = exercise_name.lower().replace("_", "-")
    from .pose_detection import LANDMARKS

    def pt(idx_key):
        idx = LANDMARKS.get(idx_key)
        return landmarks[idx] if idx is not None and idx < len(landmarks) else None

    # Default sides: left or right based on visibility
    left_hip, right_hip = pt('LEFT_HIP'), pt('RIGHT_HIP')
    left_vis = left_hip.get('visibility', 0.5) if left_hip else 0.5
    right_vis = right_hip.get('visibility', 0.5) if right_hip else 0.5
    use_left = left_vis >= right_vis

    side = 'LEFT' if use_left else 'RIGHT'

    angles = {
        'side_used': side,
        'primary_angle': 0.0,
        'secondary_angle': 0.0
    }

    if 'squat' in ex:
        hip = pt(f'{side}_HIP')
        knee = pt(f'{side}_KNEE')
        ankle = pt(f'{side}_ANKLE')
        shoulder = pt(f'{side}_SHOULDER')
        angles['knee_angle'] = calculate_angle(hip, knee, ankle)
        angles['hip_angle'] = calculate_angle(shoulder, hip, knee)
        angles['primary_angle'] = angles['knee_angle']
        angles['secondary_angle'] = angles['hip_angle']

    elif 'push' in ex:
        shoulder = pt(f'{side}_SHOULDER')
        elbow = pt(f'{side}_ELBOW')
        wrist = pt(f'{side}_WRIST')
        hip = pt(f'{side}_HIP')
        ankle = pt(f'{side}_ANKLE')
        angles['elbow_angle'] = calculate_angle(shoulder, elbow, wrist)
        angles['body_line_angle'] = calculate_angle(shoulder, hip, ankle)
        angles['primary_angle'] = angles['elbow_angle']
        angles['secondary_angle'] = angles['body_line_angle']

    elif 'lunge' in ex:
        hip = pt(f'{side}_HIP')
        knee = pt(f'{side}_KNEE')
        ankle = pt(f'{side}_ANKLE')
        angles['knee_angle'] = calculate_angle(hip, knee, ankle)
        angles['primary_angle'] = angles['knee_angle']

    elif 'curl' in ex:
        shoulder = pt(f'{side}_SHOULDER')
        elbow = pt(f'{side}_ELBOW')
        wrist = pt(f'{side}_WRIST')
        hip = pt(f'{side}_HIP')
        angles['elbow_angle'] = calculate_angle(shoulder, elbow, wrist)
        angles['shoulder_stability'] = calculate_angle(hip, shoulder, elbow)
        angles['primary_angle'] = angles['elbow_angle']
        angles['secondary_angle'] = angles['shoulder_stability']

    elif 'shoulder' in ex or 'press' in ex:
        shoulder = pt(f'{side}_SHOULDER')
        elbow = pt(f'{side}_ELBOW')
        wrist = pt(f'{side}_WRIST')
        hip = pt(f'{side}_HIP')
        angles['elbow_angle'] = calculate_angle(shoulder, elbow, wrist)
        angles['arm_elevation'] = calculate_angle(hip, shoulder, elbow)
        angles['primary_angle'] = angles['elbow_angle']
        angles['secondary_angle'] = angles['arm_elevation']

    return angles
