# Requirements Document: Timetable Generator Android App

## Introduction

The Timetable Generator Android App is a production-ready scheduling system designed for schools, colleges, and organizations. The system takes structured input about subjects, faculty, rooms, and scheduling constraints, then generates multiple valid timetables using constraint satisfaction algorithms. The app provides clear visualization of schedules from multiple perspectives (class-wise, faculty-wise, subject-wise) and allows manual editing with automatic conflict detection.

## Glossary

- **System**: The Timetable Generator Android App
- **Timetable**: A schedule grid mapping time slots (days × periods) to teaching assignments
- **Period**: A fixed time slot within a day when teaching occurs
- **Break**: A non-teaching period fixed in the schedule
- **Faculty**: A teacher who can be assigned to teach subjects
- **Subject**: A course that requires a specific number of weekly periods
- **Lab**: A subject type requiring consecutive periods in specialized rooms
- **Theory**: A subject type that can be scheduled in any available period
- **Constraint**: A rule that must be satisfied for a valid timetable
- **Slot**: A specific cell in the timetable grid (day, period)
- **Conflict**: A violation of scheduling constraints
- **Backtracking**: An algorithm technique to undo assignments and try alternatives
- **Locked_Slot**: A timetable cell that cannot be modified during regeneration
- **Room**: A physical space where teaching occurs
- **Section**: A group of students within a class
- **Workload**: The number of periods assigned to a faculty member

## Requirements

### Requirement 1: Setup Wizard Configuration

**User Story:** As a timetable administrator, I want to configure basic scheduling parameters, so that the system understands the institutional structure.

#### Acceptance Criteria

1. WHEN the administrator opens the app for the first time, THE System SHALL display a setup wizard
2. THE System SHALL collect the number of working days per week (1-7)
3. THE System SHALL collect custom names for each working day
4. THE System SHALL collect the number of periods per day (1-12)
4. THE System SHALL collect the duration of each period in minutes
5. THE System SHALL collect break period positions and durations
6. WHEN all setup data is provided, THE System SHALL validate that at least one teaching period exists per day
7. WHEN setup is complete, THE System SHALL store the configuration and proceed to subject input

### Requirement 2: Subject Management

**User Story:** As a timetable administrator, I want to define subjects with their scheduling requirements, so that the system can allocate appropriate time slots.

#### Acceptance Criteria

1. THE System SHALL allow adding multiple subjects with unique names
2. FOR each subject, THE System SHALL collect the subject name
3. FOR each subject, THE System SHALL collect the weekly required period count (1-30)
4. FOR each subject, THE System SHALL collect the subject type (Theory or Lab)
5. FOR each subject, THE System SHALL collect a priority level (High, Medium, Low)
6. FOR each subject, THE System SHALL collect whether same-day repetition is allowed
7. WHEN a Lab subject is defined, THE System SHALL require consecutive period allocation
8. WHEN subject data is incomplete, THE System SHALL prevent proceeding to the next step
9. THE System SHALL allow editing and deleting subjects before generation

### Requirement 3: Faculty Management

**User Story:** As a timetable administrator, I want to define faculty members with their constraints, so that the system respects teacher availability and workload limits.

#### Acceptance Criteria

1. THE System SHALL allow adding multiple faculty members with unique names
2. FOR each faculty member, THE System SHALL collect the name
3. FOR each faculty member, THE System SHALL collect the list of subjects they can teach
4. FOR each faculty member, THE System SHALL collect the maximum periods per day (1-12)
5. FOR each faculty member, THE System SHALL collect day-wise availability
6. FOR each faculty member, THE System SHALL collect whether to avoid consecutive periods
7. WHEN faculty data is incomplete, THE System SHALL prevent proceeding to the next step
8. THE System SHALL allow editing and deleting faculty before generation

### Requirement 4: Room and Lab Management

**User Story:** As a timetable administrator, I want to define rooms and labs with their availability, so that the system prevents double-booking and assigns appropriate spaces.

#### Acceptance Criteria

1. THE System SHALL allow adding multiple rooms with unique names
2. FOR each room, THE System SHALL collect the room name
3. FOR each room, THE System SHALL collect the room type (Classroom or Lab)
4. FOR each room, THE System SHALL collect day-wise and period-wise availability
5. WHEN a Lab room is defined, THE System SHALL support consecutive period bookings
6. THE System SHALL allow editing and deleting rooms before generation

### Requirement 5: Class and Section Management

**User Story:** As a timetable administrator, I want to define classes and sections, so that the system generates separate timetables for each group.

#### Acceptance Criteria

1. THE System SHALL allow adding multiple classes with unique names
2. FOR each class, THE System SHALL allow adding multiple sections
3. FOR each section, THE System SHALL collect the section name
4. FOR each section, THE System SHALL collect the student strength
5. THE System SHALL allow editing and deleting classes and sections before generation

### Requirement 6: Constraint Satisfaction Algorithm

**User Story:** As a system, I want to apply strict scheduling constraints, so that generated timetables are valid and conflict-free.

#### Acceptance Criteria

1. WHEN generating a timetable, THE System SHALL ensure no faculty teaches two classes simultaneously
2. WHEN assigning periods to faculty, THE System SHALL ensure daily workload does not exceed the faculty maximum
3. WHEN allocating subject periods, THE System SHALL ensure the weekly period count matches exactly the required count
4. WHEN scheduling Lab subjects, THE System SHALL allocate consecutive periods only
5. WHEN placing periods, THE System SHALL mark break periods as non-teaching and fixed
6. WHEN assigning rooms, THE System SHALL ensure no room is double-booked in the same period
7. WHEN a subject has repetition disabled, THE System SHALL avoid scheduling the same subject multiple times in one day
8. WHEN a faculty member is unavailable, THE System SHALL not assign periods during unavailable slots
9. WHEN a constraint violation is detected, THE System SHALL backtrack and try alternative assignments

### Requirement 7: Timetable Generation Process

**User Story:** As a timetable administrator, I want to generate multiple valid timetables, so that I can choose the best schedule for my institution.

#### Acceptance Criteria

1. WHEN the administrator clicks the generate button, THE System SHALL initialize an empty timetable grid
2. WHEN initializing the grid, THE System SHALL block break slots as non-teaching
3. WHEN placing subjects, THE System SHALL place Lab subjects first
4. WHEN placing subjects, THE System SHALL place high-priority subjects before lower-priority subjects
5. WHEN placing subjects, THE System SHALL place remaining subjects in priority order
6. WHEN all subjects are placed, THE System SHALL validate all constraints
7. WHEN a constraint conflict occurs, THE System SHALL backtrack and retry with shuffled assignments
8. WHEN generation is successful, THE System SHALL produce at least three timetable variants (Balanced, Compact, Faculty-friendly)
9. WHEN generation fails after maximum retries, THE System SHALL display conflict details to the administrator
10. WHILE generating, THE System SHALL display a loading indicator

### Requirement 8: Class Timetable View

**User Story:** As a timetable administrator, I want to view class-wise timetables in a grid format, so that I can see the complete schedule for each section.

#### Acceptance Criteria

1. WHEN viewing a class timetable, THE System SHALL display a grid with days as rows and periods as columns
2. WHEN displaying a timetable cell, THE System SHALL show the subject name, faculty name, and room
3. WHEN a cell represents a break, THE System SHALL display "Break" and disable interaction
4. WHEN a cell is empty, THE System SHALL display "Free Period"
5. THE System SHALL allow switching between different sections and timetable variants
6. THE System SHALL use RecyclerView or GridLayout for efficient rendering

### Requirement 9: Faculty Timetable View

**User Story:** As a faculty member, I want to view my personal schedule, so that I know when and where I need to teach.

#### Acceptance Criteria

1. WHEN viewing a faculty timetable, THE System SHALL display the teacher's schedule across all days and periods
2. WHEN displaying a faculty schedule, THE System SHALL show the class, section, subject, and room for each assigned period
3. WHEN displaying a faculty schedule, THE System SHALL show the daily workload summary
4. WHEN a faculty member has no assignment in a period, THE System SHALL display "Free"
5. THE System SHALL allow switching between different faculty members

### Requirement 10: Subject Distribution View

**User Story:** As a timetable administrator, I want to view subject distribution, so that I can verify all subjects are allocated correctly.

#### Acceptance Criteria

1. WHEN viewing subject distribution, THE System SHALL list all subjects
2. FOR each subject, THE System SHALL display the total required periods
3. FOR each subject, THE System SHALL display all assigned slots with day, period, class, and faculty
4. WHEN a subject's assigned periods do not match required periods, THE System SHALL highlight the discrepancy

### Requirement 11: Manual Timetable Editing

**User Story:** As a timetable administrator, I want to manually edit generated timetables, so that I can make adjustments while maintaining constraint validity.

#### Acceptance Criteria

1. WHEN the administrator taps a timetable cell, THE System SHALL allow locking the cell
2. WHEN a cell is locked, THE System SHALL mark it visually as locked
3. WHEN the administrator selects two cells, THE System SHALL allow swapping their contents
4. WHEN swapping cells, THE System SHALL validate that the swap does not violate constraints
5. WHEN a swap violates constraints, THE System SHALL reject the swap and display the conflict reason
6. WHEN a swap is valid, THE System SHALL update the timetable immediately
7. WHEN conflicts are detected, THE System SHALL highlight conflicting cells in red

### Requirement 12: Selective Regeneration

**User Story:** As a timetable administrator, I want to regenerate only unlocked portions of the timetable, so that I can preserve manual adjustments while improving other areas.

#### Acceptance Criteria

1. WHEN the administrator requests regeneration, THE System SHALL preserve all locked slots
2. WHEN regenerating, THE System SHALL only modify unlocked slots
3. WHEN regenerating with locked slots, THE System SHALL treat locked slots as fixed constraints
4. WHEN regeneration with locked slots fails, THE System SHALL notify the administrator that locked slots may be causing conflicts

### Requirement 13: Conflict Detection and Reporting

**User Story:** As a timetable administrator, I want to see detailed conflict information, so that I can understand why generation failed or why edits are rejected.

#### Acceptance Criteria

1. WHEN a constraint violation occurs, THE System SHALL identify the specific constraint violated
2. WHEN reporting conflicts, THE System SHALL display the conflicting faculty, subject, room, or time slot
3. WHEN multiple conflicts exist, THE System SHALL list all conflicts
4. WHEN displaying conflicts, THE System SHALL highlight affected cells in red
5. THE System SHALL provide actionable suggestions to resolve conflicts

### Requirement 14: Data Persistence

**User Story:** As a timetable administrator, I want my configuration and generated timetables to be saved, so that I can return to them later.

#### Acceptance Criteria

1. WHEN the administrator completes setup, THE System SHALL save the configuration in memory
2. WHEN a timetable is generated, THE System SHALL store it in memory for the current session
3. WHERE Room Database is implemented, THE System SHALL persist configuration and timetables to local storage
4. WHERE Room Database is implemented, WHEN the app restarts, THE System SHALL load the last saved state

### Requirement 15: MVVM Architecture Implementation

**User Story:** As a developer, I want the app to follow MVVM architecture, so that the codebase is maintainable and testable.

#### Acceptance Criteria

1. THE System SHALL separate UI logic into Activities and Fragments under ui/ package
2. THE System SHALL implement ViewModels under viewmodel/ package
3. THE System SHALL define data models as POJOs under model/ package
4. THE System SHALL implement data access logic under repository/ package
5. THE System SHALL implement algorithm logic under algorithm/ package
6. THE System SHALL use LiveData for communication between ViewModels and UI
7. THE System SHALL ensure algorithm logic is independent of Android UI components

### Requirement 16: Code Quality and Completeness

**User Story:** As a developer, I want production-ready code, so that the app can be deployed without additional scaffolding.

#### Acceptance Criteria

1. THE System SHALL contain no placeholder methods or TODO comments
2. THE System SHALL contain no incomplete method implementations
3. THE System SHALL compile without errors in Android Studio
4. THE System SHALL run on Android devices without crashes
5. THE System SHALL use only Java (no Kotlin)
6. THE System SHALL not depend on third-party timetable generation libraries
7. THE System SHALL implement all constraint validation logic from scratch

### Requirement 17: User Interface Implementation

**User Story:** As a timetable administrator, I want a functional and clear user interface, so that I can efficiently input data and view results.

#### Acceptance Criteria

1. THE System SHALL use XML layouts for all UI screens
2. THE System SHALL provide clear labels and input validation messages
3. THE System SHALL use RecyclerView for displaying lists of subjects, faculty, and rooms
4. THE System SHALL use GridLayout or RecyclerView for timetable grid display
5. THE System SHALL provide navigation between setup, input, generation, and view screens
6. THE System SHALL display loading indicators during timetable generation
7. WHEN input validation fails, THE System SHALL display error messages near the relevant input fields

### Requirement 18: Timetable Variant Generation

**User Story:** As a timetable administrator, I want multiple timetable variants with different optimization goals, so that I can choose the schedule that best fits institutional needs.

#### Acceptance Criteria

1. WHEN generating timetables, THE System SHALL produce a "Balanced" variant that distributes subjects evenly across days
2. WHEN generating timetables, THE System SHALL produce a "Compact" variant that minimizes gaps and free periods
3. WHEN generating timetables, THE System SHALL produce a "Faculty-friendly" variant that respects faculty preferences for avoiding consecutive periods
4. THE System SHALL allow the administrator to switch between variants
5. THE System SHALL clearly label each variant with its optimization strategy
