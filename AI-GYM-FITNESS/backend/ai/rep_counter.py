"""
AI GYM & FITNESS - Repetition Counter State Machines
Finite State Automata ensuring debounced, valid repetition increments
across all 5 target movement disciplines.
"""

class RepCounter:
    """
    Tracks state transitions and counts completed repetitions.
    Ensures movements pass through proper eccentric & concentric thresholds.
    """
    def __init__(self, exercise_name: str = 'squats'):
        self.exercise_name = exercise_name.lower().replace("_", "-")
        self.state = 'INITIAL'
        self.rep_count = 0
        self.valid_reps = 0
        self.invalid_reps = 0
        self.stage_history = []
        self.last_rep_form_score = 100

    def reset(self):
        self.state = 'INITIAL'
        self.rep_count = 0
        self.valid_reps = 0
        self.invalid_reps = 0
        self.stage_history = []
        self.last_rep_form_score = 100

    def update(self, angles: dict, form_valid: bool = True, rep_score: float = 90.0):
        """
        Updates movement state machine with current joint angle.
        Returns: (rep_completed_boolean, rep_count, state, valid_reps)
        """
        primary = angles.get('primary_angle', 180.0)
        rep_completed = False
        ex = self.exercise_name

        if 'squat' in ex or 'lunge' in ex:
            # SQUAT / LUNGE: Standing (160+) -> Descending (140-100) -> Bottom (<=95) -> Ascending (120+) -> Standing (160+)
            if primary >= 155:
                if self.state == 'ASCENDING' or self.state == 'BOTTOM':
                    self.rep_count += 1
                    if form_valid and rep_score >= 65:
                        self.valid_reps += 1
                    else:
                        self.invalid_reps += 1
                    self.last_rep_form_score = rep_score
                    rep_completed = True
                self.state = 'STANDING'
            elif primary < 95 and self.state in ('STANDING', 'DESCENDING'):
                self.state = 'BOTTOM'
            elif primary < 140 and self.state == 'STANDING':
                self.state = 'DESCENDING'
            elif primary > 115 and self.state == 'BOTTOM':
                self.state = 'ASCENDING'

        elif 'push' in ex:
            # PUSH-UP: Up (>150) -> Descending -> Down (<=90) -> Ascending -> Up (>150)
            if primary >= 150:
                if self.state == 'ASCENDING' or self.state == 'BOTTOM':
                    self.rep_count += 1
                    if form_valid and rep_score >= 65:
                        self.valid_reps += 1
                    else:
                        self.invalid_reps += 1
                    self.last_rep_form_score = rep_score
                    rep_completed = True
                self.state = 'UP'
            elif primary <= 90 and self.state in ('UP', 'DESCENDING'):
                self.state = 'BOTTOM'
            elif primary < 135 and self.state == 'UP':
                self.state = 'DESCENDING'
            elif primary > 110 and self.state == 'BOTTOM':
                self.state = 'ASCENDING'

        elif 'curl' in ex:
            # BICEP CURL: Extended (>145) -> Curling -> Peak Contraction (<=45) -> Lowering -> Extended
            if primary >= 145:
                if self.state == 'LOWERING' or self.state == 'PEAK':
                    self.rep_count += 1
                    if form_valid and rep_score >= 65:
                        self.valid_reps += 1
                    else:
                        self.invalid_reps += 1
                    self.last_rep_form_score = rep_score
                    rep_completed = True
                self.state = 'EXTENDED'
            elif primary <= 48 and self.state in ('EXTENDED', 'CURLING'):
                self.state = 'PEAK'
            elif primary < 120 and self.state == 'EXTENDED':
                self.state = 'CURLING'
            elif primary > 80 and self.state == 'PEAK':
                self.state = 'LOWERING'

        elif 'shoulder' in ex or 'press' in ex:
            # SHOULDER PRESS: Bottom Rack (<=90) -> Pressing -> Top Extension (>=155) -> Lowering -> Bottom Rack
            if primary >= 155:
                self.state = 'OVERHEAD'
            elif primary <= 90:
                if self.state == 'LOWERING' or self.state == 'OVERHEAD':
                    self.rep_count += 1
                    if form_valid and rep_score >= 65:
                        self.valid_reps += 1
                    else:
                        self.invalid_reps += 1
                    self.last_rep_form_score = rep_score
                    rep_completed = True
                self.state = 'RACK'
            elif primary > 105 and self.state == 'RACK':
                self.state = 'PRESSING'
            elif primary < 140 and self.state == 'OVERHEAD':
                self.state = 'LOWERING'

        return rep_completed, self.rep_count, self.state, self.valid_reps
