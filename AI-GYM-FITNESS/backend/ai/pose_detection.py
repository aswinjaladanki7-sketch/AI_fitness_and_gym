"""
AI GYM & FITNESS - Pose Detection Engine
Wraps MediaPipe Pose and provides resilient landmark extraction,
coordinate normalization, and landmark indexing.
"""

import math

# MediaPipe landmark indices
LANDMARKS = {
    'NOSE': 0,
    'LEFT_EYE_INNER': 1,
    'LEFT_EYE': 2,
    'LEFT_EYE_OUTER': 3,
    'RIGHT_EYE_INNER': 4,
    'RIGHT_EYE': 5,
    'RIGHT_EYE_OUTER': 6,
    'LEFT_EAR': 7,
    'RIGHT_EAR': 8,
    'MOUTH_LEFT': 9,
    'MOUTH_RIGHT': 10,
    'LEFT_SHOULDER': 11,
    'RIGHT_SHOULDER': 12,
    'LEFT_ELBOW': 13,
    'RIGHT_ELBOW': 14,
    'LEFT_WRIST': 15,
    'RIGHT_WRIST': 16,
    'LEFT_PINKY': 17,
    'RIGHT_PINKY': 18,
    'LEFT_INDEX': 19,
    'RIGHT_INDEX': 20,
    'LEFT_THUMB': 21,
    'RIGHT_THUMB': 22,
    'LEFT_HIP': 23,
    'RIGHT_HIP': 24,
    'LEFT_KNEE': 25,
    'RIGHT_KNEE': 26,
    'LEFT_ANKLE': 27,
    'RIGHT_ANKLE': 28,
    'LEFT_HEEL': 29,
    'RIGHT_HEEL': 30,
    'LEFT_FOOT_INDEX': 31,
    'RIGHT_FOOT_INDEX': 32
}

class PoseDetector:
    """
    Initializes MediaPipe Pose if available, or processes landmark dictionaries
    received from frontend / API payloads.
    """
    def __init__(self, min_detection_confidence=0.5, min_tracking_confidence=0.5):
        self.mp_pose = None
        self.pose = None
        try:
            import mediapipe as mp
            self.mp_pose = mp.solutions.pose
            self.pose = self.mp_pose.Pose(
                min_detection_confidence=min_detection_confidence,
                min_tracking_confidence=min_tracking_confidence
            )
        except Exception:
            # Running in lightweight or client-processed mode
            pass

    def process_frame(self, image_bgr):
        """Processes OpenCV BGR image frame and returns landmarks."""
        if not self.pose:
            return None
        import cv2
        image_rgb = cv2.cvtColor(image_bgr, cv2.COLOR_BGR2RGB)
        results = self.pose.process(image_rgb)
        if not results.pose_landmarks:
            return None

        landmarks = []
        for lm in results.pose_landmarks.landmark:
            landmarks.append({
                'x': lm.x,
                'y': lm.y,
                'z': lm.z,
                'visibility': lm.visibility
            })
        return landmarks

    @staticmethod
    def get_landmark_point(landmarks, name: str):
        """Retrieves point dictionary by landmark key name."""
        idx = LANDMARKS.get(name.upper())
        if idx is not None and idx < len(landmarks):
            return landmarks[idx]
        return None
