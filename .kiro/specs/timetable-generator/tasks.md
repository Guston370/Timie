# Implementation Plan: Timetable Generator Android App

## Overview

This implementation plan breaks down the Timetable Generator Android App into discrete, actionable coding tasks. The app follows MVVM architecture and implements a constraint satisfaction solver for academic scheduling. Tasks are organized to build incrementally: starting with data models, then repository layer, algorithm layer, ViewModels, and finally UI components. Each task builds on previous work to ensure continuous integration.

The implementation uses Java for Android and includes both unit tests and property-based tests to ensure correctness. Property-based tests use JUnit-Quickcheck and run with minimum 100 iterations per test.

## Tasks

- [x] 1. Set up project structure and dependencies
  - Create Android project with Java
  - Set up package structure (model/, repository/, algorithm/, viewmodel/, ui/, utils/)
  - Enable ViewBinding in build.gradle
  - Add dependencies: AndroidX, LiveData, ViewModel, RecyclerView
  - Add JUnit-Quickcheck dependencies for property-based testing
  - _Requirements: 15.1, 15.3, 16.5_

- [x] 2. Implement core data model classes
  - [x] 2.1 Create Config.java model
    - Implement fields: workingDays, dayNames, periodsPerDay, periodDuration, breakPeriods
    - Add getters, setters, and validation methods
    - _Requirements: 1.2, 1.3, 1.4, 1.5, 1.6_
  
  - [x] 2.2 Create Subject.java model
    - Implement fields: id, name, weeklyPeriods, type (enum: THEORY/LAB), priority (enum: HIGH/MEDIUM/LOW), allowRepetition
    - Add getters, setters, and validation
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6_
  
  - [x] 2.3 Create Faculty.java model
    - Implement fields: id, name, subjectIds, maxPeriodsPerDay, availability (Map<Integer, List<Integer>>), avoidConsecutive
    - Add getters, setters, and validation
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_
  
  - [x] 2.4 Create Room.java model
    - Implement fields: id, name, type (enum: CLASSROOM/LAB), availability (Map<Integer, List<Integer>>)
    - Add getters, setters
    - _Requirements: 4.1, 4.2, 4.3, 4.4_
  
  - [x] 2.5 Create ClassSection.java model
    - Implement fields: id, className, sectionName, studentStrength
    - Add getters, setters
    - _Requirements: 5.1, 5.2, 5.3, 5.4_
  
  - [x] 2.6 Create TimeSlot.java model
    - Implement fields: day, period
    - Override equals() and hashCode() for proper map key behavior
    - _Requirements: 6.5, 8.1_
  
  - [x] 2.7 Create Assignment.java model
    - Implement fields: subjectId, facultyId, roomId, classSectionId, timeSlot, locked
    - Add getters, setters
    - _Requirements: 6.1, 11.1, 11.2_
  
  - [x] 2.8 Create Timetable.java model
    - Implement fields: id, variantName, classTimetables (Map<String, Map<TimeSlot, Assignment>>), facultyTimetables, allAssignments
    - Add methods: addAssignment(), removeAssignment(), getAssignment()
    - _Requirements: 7.1, 8.1, 9.1, 18.1_
  
  - [x] 2.9 Create ConflictReport.java model
    - Implement fields: conflicts (List<String>), conflictingSlots (List<TimeSlot>), suggestion
    - Add methods to add conflicts and suggestions
    - _Requirements: 13.1, 13.2, 13.3_

- [x] 3. Implement repository layer for data management
  - [x] 3.1 Create TimetableRepository.java
    - Implement in-memory storage using HashMap for subjects, faculties, rooms, classSections
    - Implement CRUD methods: addSubject(), addFaculty(), addRoom(), addClassSection()
    - Implement getter methods: getAllSubjects(), getAllFaculties(), getAllRooms(), getAllClassSections()
    - Implement methods: saveTimetable(), getAllTimetables()
    - Use singleton pattern for repository instance
    - _Requirements: 14.1, 14.2, 15.4_
  
  - [x] 3.2 Create ConfigRepository.java
    - Implement in-memory storage for Config object
    - Implement saveConfig() and getConfig() methods
    - Use singleton pattern
    - _Requirements: 1.7, 14.1_

- [x] 4. Implement constraint validation system
  - [x] 4.1 Create Constraint.java interface
    - Define isSatisfied(Assignment, Timetable) method
    - Define getViolationMessage() method
    - _Requirements: 6.9, 13.1_
  
  - [x] 4.2 Implement NoFacultyDoubleBookingConstraint.java
    - Check if faculty is already assigned at the same time slot
    - Return violation message if constraint fails
    - _Requirements: 6.1_
  
  - [x] 4.3 Implement FacultyWorkloadConstraint.java
    - Count faculty's periods on the assignment day
    - Compare against faculty's maxPeriodsPerDay
    - _Requirements: 6.2_
  
  - [x] 4.4 Implement LabConsecutiveConstraint.java
    - Check if subject type is LAB
    - Verify next period is available for same class, faculty, and room
    - _Requirements: 6.4, 2.7_
  
  - [x] 4.5 Implement RoomDoubleBookingConstraint.java
    - Check if room is already booked at the same time slot
    - _Requirements: 6.6_
  
  - [x] 4.6 Implement SubjectRepetitionConstraint.java
    - Check subject's allowRepetition flag
    - If false, ensure subject not already scheduled for class on same day
    - _Requirements: 6.7, 2.6_
  
  - [x] 4.7 Implement FacultyAvailabilityConstraint.java
    - Check faculty's availability map for the time slot
    - _Requirements: 6.8, 3.5_
  
  - [x] 4.8 Implement BreakPeriodConstraint.java
    - Ensure assignments are not placed in break periods
    - _Requirements: 6.5_
  
  - [x] 4.9 Create ConstraintChecker.java
    - Initialize list of all constraint implementations
    - Implement checkAllConstraints(Assignment, Timetable) method
    - Implement getViolations(Assignment, Timetable) method that returns list of violation messages
    - _Requirements: 6.9, 13.1, 13.2_


- [x] 5. Create property-based test generators
  - [x] 5.1 Create ConfigGenerator.java for JUnit-Quickcheck
    - Generate random Config objects with valid constraints
    - Ensure workingDays between 1-7, periodsPerDay between 1-12
    - Generate random break periods that leave at least one teaching period per day
    - _Requirements: 1.2, 1.3, 1.4, 1.5, 1.6_
  
  - [x] 5.2 Create SubjectGenerator.java
    - Generate random Subject objects with valid data
    - Generate random subject types (THEORY/LAB), priorities, weekly periods (1-30)
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6_
  
  - [x] 5.3 Create FacultyGenerator.java
    - Generate random Faculty objects with valid availability
    - Ensure at least one subject assigned, at least some available time slots
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_
  
  - [x] 5.4 Create RoomGenerator.java
    - Generate random Room objects with valid availability
    - _Requirements: 4.1, 4.2, 4.3, 4.4_
  
  - [x] 5.5 Create ClassSectionGenerator.java
    - Generate random ClassSection objects
    - _Requirements: 5.1, 5.2, 5.3, 5.4_
  
  - [x] 5.6 Create TimetableGenerator.java for testing
    - Generate random valid Timetable objects for testing
    - Use constraint checker to ensure generated timetables are valid
    - _Requirements: 7.1, 8.1_

- [x] 6. Checkpoint - Ensure models and generators compile
  - Verify all model classes compile without errors
  - Verify repository classes compile without errors
  - Verify all constraint classes compile without errors
  - Verify all generator classes compile without errors
  - Ask the user if questions arise

- [x] 7. Implement backtracking solver algorithm
  - [x] 7.1 Create HeuristicStrategy.java interface
    - Define sortAssignments(List<Assignment>) method
    - Define selectBestSlot(Assignment, List<TimeSlot>) method
    - _Requirements: 7.3, 7.4, 7.5, 18.1_
  
  - [x] 7.2 Implement BalancedStrategy.java
    - Sort assignments: Labs first, then by priority (HIGH > MEDIUM > LOW)
    - Select slots that balance distribution across days
    - _Requirements: 7.3, 7.4, 18.1_
  
  - [x] 7.3 Implement CompactStrategy.java
    - Prefer slots that minimize gaps between periods
    - Select earliest available slots on days with existing assignments
    - _Requirements: 18.2_
  
  - [x] 7.4 Implement FacultyFriendlyStrategy.java
    - Respect faculty's avoidConsecutive preference
    - Filter out slots adjacent to existing faculty assignments when avoidConsecutive is true
    - _Requirements: 18.3, 3.6_
  
  - [x] 7.5 Create BacktrackingSolver.java
    - Implement solve(HeuristicStrategy) method
    - Implement backtrack(Timetable, List<Assignment>, int) recursive method
    - Implement generateAssignments() to create all required assignments from subjects and classes
    - Implement getAllPossibleSlots() to generate valid time slots (excluding breaks)
    - Implement findAvailableFaculty(Subject) to match faculty to subjects
    - Implement findAppropriateRoom(Subject) to match rooms to subject types
    - Use ConstraintChecker to validate each assignment before placing
    - Backtrack when constraints fail
    - _Requirements: 6.9, 7.1, 7.2, 7.6, 7.7, 15.7_
  
  - [x] 7.6 Implement solveWithFixed() method in BacktrackingSolver
    - Accept existing timetable with locked assignments
    - Treat locked assignments as fixed constraints
    - Generate only unlocked assignments
    - _Requirements: 12.1, 12.2, 12.3_


- [x] 8. Implement timetable generator with variant support
  - [x] 8.1 Create TimetableGenerator.java
    - Inject BacktrackingSolver and TimetableRepository
    - Implement generateVariants() method
    - Generate three variants: Balanced, Compact, Faculty-friendly
    - Set variantName for each generated timetable
    - Return list of successfully generated timetables
    - _Requirements: 7.8, 18.1, 18.2, 18.3, 18.4_
  
  - [x] 8.2 Implement regenerateWithLockedSlots() method
    - Copy locked assignments from existing timetable
    - Generate assignments for unlocked slots only
    - Call solveWithFixed() with locked constraints
    - _Requirements: 12.1, 12.2, 12.3, 12.4_
  
  - [x] 8.3 Add retry logic with shuffling
    - If generation fails, shuffle assignment order and retry
    - Limit maximum retries (e.g., 5 attempts)
    - Return ConflictReport if all retries fail
    - _Requirements: 7.7, 7.9_

- [x] 9. Write property tests for core constraints
  - [x] 9.1 Write property test for No Faculty Double-Booking
    - **Property 1: No Faculty Double-Booking**
    - Generate random valid timetables, verify no faculty teaches two classes simultaneously
    - Use @Property annotation with trials = 100
    - Tag: // Feature: timetable-generator, Property 1: No Faculty Double-Booking
    - **Validates: Requirements 6.1**
  
  - [x] 9.2 Write property test for Faculty Workload Limits
    - **Property 2: Faculty Workload Limits**
    - Generate random timetables and faculty, verify daily workload ≤ maxPeriodsPerDay
    - Tag: // Feature: timetable-generator, Property 2: Faculty Workload Limits
    - **Validates: Requirements 6.2**
  
  - [x] 9.3 Write property test for Subject Weekly Period Count
    - **Property 3: Subject Weekly Period Count**
    - Generate random timetables, verify assigned periods = required periods for each subject
    - Tag: // Feature: timetable-generator, Property 3: Subject Weekly Period Count
    - **Validates: Requirements 6.3**
  
  - [x] 9.4 Write property test for Lab Consecutive Periods
    - **Property 4: Lab Consecutive Periods**
    - Generate random timetables with Lab subjects, verify consecutive allocation
    - Tag: // Feature: timetable-generator, Property 4: Lab Consecutive Periods
    - **Validates: Requirements 2.7, 6.4**
  
  - [x] 9.5 Write property test for Break Periods Unassigned
    - **Property 5: Break Periods Unassigned**
    - Generate random timetables with break periods, verify no assignments in breaks
    - Tag: // Feature: timetable-generator, Property 5: Break Periods Unassigned
    - **Validates: Requirements 6.5, 7.2**
  
  - [x] 9.6 Write property test for No Room Double-Booking
    - **Property 6: No Room Double-Booking**
    - Generate random timetables, verify no room is assigned to two classes simultaneously
    - Tag: // Feature: timetable-generator, Property 6: No Room Double-Booking
    - **Validates: Requirements 6.6**
  
  - [x] 9.7 Write property test for Subject Repetition Constraint
    - **Property 7: Subject Repetition Constraint**
    - Generate random timetables with repetition-disabled subjects, verify no same-day repetition
    - Tag: // Feature: timetable-generator, Property 7: Subject Repetition Constraint
    - **Validates: Requirements 6.7**
  
  - [x] 9.8 Write property test for Faculty Availability Constraint
    - **Property 8: Faculty Availability Constraint**
    - Generate random timetables and faculty availability, verify assignments only during available slots
    - Tag: // Feature: timetable-generator, Property 8: Faculty Availability Constraint
    - **Validates: Requirements 6.8**

- [ ] 10. Checkpoint - Test algorithm layer
  - Verify BacktrackingSolver compiles and runs
  - Verify TimetableGenerator compiles
  - Verify constraint checking works correctly
  - Run property tests to validate core constraints
  - Ask the user if questions arise


- [x] 11. Implement ViewModels for UI state management
  - [x] 11.1 Create SetupViewModel.java
    - Extend AndroidX ViewModel
    - Create MutableLiveData<Config> for configuration
    - Implement saveConfig() method that validates and saves to ConfigRepository
    - Expose LiveData<Config> for UI observation
    - _Requirements: 1.7, 15.2, 15.6_
  
  - [x] 11.2 Create SubjectViewModel.java
    - Create MutableLiveData<List<Subject>> for subject list
    - Implement addSubject(), editSubject(), deleteSubject() methods
    - Validate subject data before saving to repository
    - _Requirements: 2.8, 2.9, 15.2_
  
  - [x] 11.3 Create FacultyViewModel.java
    - Create MutableLiveData<List<Faculty>> for faculty list
    - Implement addFaculty(), editFaculty(), deleteFaculty() methods
    - Validate faculty data before saving
    - _Requirements: 3.7, 3.8, 15.2_
  
  - [x] 11.4 Create RoomViewModel.java
    - Create MutableLiveData<List<Room>> for room list
    - Implement addRoom(), editRoom(), deleteRoom() methods
    - _Requirements: 4.6, 15.2_
  
  - [x] 11.5 Create ClassViewModel.java
    - Create MutableLiveData<List<ClassSection>> for class sections
    - Implement addClass(), editClass(), deleteClass() methods
    - _Requirements: 5.5, 15.2_
  
  - [x] 11.6 Create GenerationViewModel.java
    - Inject TimetableGenerator
    - Create MutableLiveData<List<Timetable>> for generated timetables
    - Create MutableLiveData<Boolean> for loading state
    - Create MutableLiveData<ConflictReport> for generation errors
    - Implement generateTimetables() method that runs on background thread
    - Post results or conflicts to LiveData
    - _Requirements: 7.8, 7.9, 7.10, 15.2, 15.6_
  
  - [x] 11.7 Create TimetableViewModel.java
    - Create MutableLiveData<Timetable> for current timetable
    - Create MutableLiveData<ConflictReport> for edit conflicts
    - Implement swapAssignments(Assignment, Assignment) method
    - Validate swap using ConstraintChecker before applying
    - Revert swap if validation fails and post conflict report
    - Implement lockAssignment(Assignment) method
    - _Requirements: 11.3, 11.4, 11.5, 11.6, 15.2, 15.6_

- [x] 12. Write property tests for validation
  - [x] 12.1 Write property test for Configuration Validation
    - **Property 9: Configuration Validation**
    - Generate random configs with break periods, verify at least one teaching period per day
    - Tag: // Feature: timetable-generator, Property 9: Configuration Validation
    - **Validates: Requirements 1.7**
  
  - [x] 12.2 Write property test for Subject Data Validation
    - **Property 10: Subject Data Validation**
    - Generate random incomplete subjects, verify validation rejects them
    - Tag: // Feature: timetable-generator, Property 10: Subject Data Validation
    - **Validates: Requirements 2.8**
  
  - [x] 12.3 Write property test for Faculty Data Validation
    - **Property 11: Faculty Data Validation**
    - Generate random incomplete faculty, verify validation rejects them
    - Tag: // Feature: timetable-generator, Property 11: Faculty Data Validation
    - **Validates: Requirements 3.7**

- [x] 13. Implement setup wizard UI
  - [x] 13.1 Create activity_setup_wizard.xml layout
    - Add EditText fields for working days, day names, periods per day, period duration
    - Add RecyclerView or input fields for break periods
    - Add "Next" button
    - _Requirements: 1.1, 17.1, 17.2_
  
  - [x] 13.2 Create SetupWizardActivity.java
    - Extend AppCompatActivity
    - Initialize SetupViewModel
    - Bind UI elements using ViewBinding
    - Observe Config LiveData from ViewModel
    - Validate input when "Next" is clicked
    - Display error messages for invalid input
    - Navigate to SubjectInputActivity on success
    - _Requirements: 1.1, 1.6, 1.7, 15.1, 17.2, 17.7_


- [x] 14. Implement subject input UI
  - [x] 14.1 Create activity_subject_input.xml layout
    - Add RecyclerView for subject list
    - Add FloatingActionButton to add new subject
    - Add "Next" button to proceed
    - _Requirements: 2.1, 17.1, 17.3_
  
  - [x] 14.2 Create dialog_subject_input.xml layout
    - Add EditText for subject name, weekly periods
    - Add Spinner for subject type (Theory/Lab)
    - Add Spinner for priority (High/Medium/Low)
    - Add CheckBox for allow repetition
    - Add "Save" and "Cancel" buttons
    - _Requirements: 2.2, 2.3, 2.4, 2.5, 2.6, 17.2_
  
  - [x] 14.3 Create SubjectAdapter.java (RecyclerView.Adapter)
    - Display subject name, type, weekly periods, priority
    - Add edit and delete buttons for each item
    - _Requirements: 2.9, 17.3_
  
  - [x] 14.4 Create SubjectInputActivity.java
    - Initialize SubjectViewModel
    - Set up RecyclerView with SubjectAdapter
    - Observe subject list LiveData
    - Show dialog on FAB click to add subject
    - Validate that at least one subject exists before proceeding
    - Navigate to FacultyInputActivity on "Next"
    - _Requirements: 2.1, 2.8, 2.9, 15.1, 17.5_

- [x] 15. Implement faculty input UI
  - [x] 15.1 Create activity_faculty_input.xml layout
    - Add RecyclerView for faculty list
    - Add FloatingActionButton to add new faculty
    - Add "Next" button
    - _Requirements: 3.1, 17.1, 17.3_
  
  - [x] 15.2 Create dialog_faculty_input.xml layout
    - Add EditText for faculty name, max periods per day
    - Add multi-select list for subjects they can teach
    - Add day-wise availability checkboxes
    - Add CheckBox for avoid consecutive periods
    - Add "Save" and "Cancel" buttons
    - _Requirements: 3.2, 3.3, 3.4, 3.5, 3.6, 17.2_
  
  - [x] 15.3 Create FacultyAdapter.java
    - Display faculty name, subjects, max periods
    - Add edit and delete buttons
    - _Requirements: 3.8, 17.3_
  
  - [x] 15.4 Create FacultyInputActivity.java
    - Initialize FacultyViewModel
    - Set up RecyclerView with FacultyAdapter
    - Show dialog on FAB click
    - Validate faculty data before proceeding
    - Navigate to RoomInputActivity on "Next"
    - _Requirements: 3.1, 3.7, 3.8, 15.1_

- [x] 16. Implement room input UI
  - [x] 16.1 Create activity_room_input.xml layout
    - Add RecyclerView for room list
    - Add FloatingActionButton to add new room
    - Add "Next" button
    - _Requirements: 4.1, 17.1, 17.3_
  
  - [x] 16.2 Create dialog_room_input.xml layout
    - Add EditText for room name
    - Add Spinner for room type (Classroom/Lab)
    - Add day-wise and period-wise availability grid
    - Add "Save" and "Cancel" buttons
    - _Requirements: 4.2, 4.3, 4.4, 17.2_
  
  - [x] 16.3 Create RoomAdapter.java
    - Display room name and type
    - Add edit and delete buttons
    - _Requirements: 4.6, 17.3_
  
  - [x] 16.4 Create RoomInputActivity.java
    - Initialize RoomViewModel
    - Set up RecyclerView with RoomAdapter
    - Show dialog on FAB click
    - Navigate to ClassInputActivity on "Next"
    - _Requirements: 4.1, 4.6, 15.1_

- [x] 17. Implement class and section input UI
  - [x] 17.1 Create activity_class_input.xml layout
    - Add RecyclerView for class sections
    - Add FloatingActionButton to add new class
    - Add "Generate Timetable" button
    - _Requirements: 5.1, 17.1, 17.3_
  
  - [x] 17.2 Create dialog_class_input.xml layout
    - Add EditText for class name, section name, student strength
    - Add "Save" and "Cancel" buttons
    - _Requirements: 5.3, 5.4, 17.2_
  
  - [x] 17.3 Create ClassSectionAdapter.java
    - Display class name, section name, student strength
    - Add edit and delete buttons
    - _Requirements: 5.5, 17.3_
  
  - [x] 17.4 Create ClassInputActivity.java
    - Initialize ClassViewModel
    - Set up RecyclerView with ClassSectionAdapter
    - Show dialog on FAB click
    - Navigate to GenerationActivity on "Generate Timetable"
    - _Requirements: 5.1, 5.5, 15.1_

- [x] 18. Checkpoint - Test input flow
  - Verify all input activities compile and run
  - Verify navigation flow works correctly
  - Verify data is saved to repositories
  - Ask the user if questions arise


- [x] 19. Implement timetable generation UI
  - [x] 19.1 Create activity_generation.xml layout
    - Add ProgressBar for loading indicator
    - Add "Generate" button
    - Add RecyclerView to display generated variants
    - Add TextView for conflict messages
    - _Requirements: 7.10, 17.1, 17.6_
  
  - [x] 19.2 Create VariantsAdapter.java
    - Display variant name (Balanced, Compact, Faculty-friendly)
    - Add "View" button for each variant
    - _Requirements: 18.4, 18.5, 17.3_
  
  - [x] 19.3 Create GenerationActivity.java
    - Initialize GenerationViewModel
    - Observe isGenerating LiveData to show/hide loading indicator
    - Observe timetables LiveData to display variants
    - Observe conflicts LiveData to show error dialog
    - Call viewModel.generateTimetables() on button click
    - Navigate to ClassTimetableActivity when variant is selected
    - _Requirements: 7.8, 7.9, 7.10, 15.1, 17.6_
  
  - [x] 19.4 Create conflict dialog layout and display logic
    - Show conflict details from ConflictReport
    - Display suggestions to resolve conflicts
    - _Requirements: 13.1, 13.2, 13.3, 13.5_

- [ ] 20. Write property tests for generation and variants
  - [x] 20.1 Write property test for Variant Generation Count
    - **Property 12: Variant Generation Count**
    - Generate random valid configurations, verify three variants are produced
    - Tag: // Feature: timetable-generator, Property 12: Variant Generation Count
    - **Validates: Requirements 7.8**
  
  - [x] 20.2 Write property test for Balanced Distribution
    - **Property 13: Balanced Distribution**
    - Generate timetables with Balanced strategy, verify even distribution across days
    - Tag: // Feature: timetable-generator, Property 13: Balanced Distribution
    - **Validates: Requirements 18.1**
  
  - [x] 20.3 Write property test for Compact Schedule
    - **Property 14: Compact Schedule**
    - Generate timetables with Compact strategy, verify minimized gaps
    - Tag: // Feature: timetable-generator, Property 14: Compact Schedule
    - **Validates: Requirements 18.2**
  
  - [x] 20.4 Write property test for Faculty-Friendly Scheduling
    - **Property 15: Faculty-Friendly Scheduling**
    - Generate timetables with Faculty-friendly strategy, verify no consecutive periods for faculty with avoidConsecutive
    - Tag: // Feature: timetable-generator, Property 15: Faculty-Friendly Scheduling
    - **Validates: Requirements 18.3**

- [x] 21. Implement class timetable view UI
  - [x] 21.1 Create activity_class_timetable.xml layout
    - Add Spinner to select class section
    - Add Spinner to select timetable variant
    - Add RecyclerView with GridLayoutManager for timetable grid
    - Add navigation buttons to other views (Faculty, Subject Distribution)
    - _Requirements: 8.1, 8.5, 17.1, 17.4_
  
  - [x] 21.2 Create timetable_cell.xml layout
    - Add TextViews for subject name, faculty name, room
    - Add visual indicator for locked cells
    - Add visual indicator for break periods
    - _Requirements: 8.2, 8.3, 8.4, 11.2_
  
  - [x] 21.3 Create TimetableGridAdapter.java
    - Extend RecyclerView.Adapter
    - Calculate day and period from position
    - Display assignment details in each cell
    - Show "Break" for break periods and disable interaction
    - Show "Free Period" for empty cells
    - Apply locked cell styling
    - Handle cell click events for editing
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.6, 17.4_
  
  - [x] 21.4 Create ClassTimetableActivity.java
    - Initialize TimetableViewModel
    - Set up RecyclerView with GridLayoutManager
    - Observe currentTimetable LiveData
    - Update adapter when class section or variant changes
    - Handle cell clicks to show edit options (lock, swap)
    - Navigate to EditTimetableActivity for manual editing
    - _Requirements: 8.1, 8.5, 8.6, 15.1_

- [x] 22. Implement faculty timetable view UI
  - [x] 22.1 Create activity_faculty_timetable.xml layout
    - Add Spinner to select faculty member
    - Add RecyclerView with GridLayoutManager for faculty schedule
    - Add TextView for daily workload summary
    - _Requirements: 9.1, 9.3, 17.1, 17.4_
  
  - [x] 22.2 Create FacultyTimetableAdapter.java
    - Display class, section, subject, room for each assigned period
    - Show "Free" for unassigned periods
    - Calculate and display daily workload
    - _Requirements: 9.2, 9.3, 9.4, 17.4_
  
  - [x] 22.3 Create FacultyTimetableActivity.java
    - Initialize TimetableViewModel
    - Set up RecyclerView with GridLayoutManager
    - Observe currentTimetable LiveData
    - Update adapter when faculty selection changes
    - Display workload summary
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 15.1_


- [x] 23. Implement subject distribution view UI
  - [x] 23.1 Create activity_subject_distribution.xml layout
    - Add RecyclerView for subject list
    - _Requirements: 10.1, 17.1, 17.3_
  
  - [x] 23.2 Create item_subject_distribution.xml layout
    - Add TextViews for subject name, required periods, assigned periods
    - Add RecyclerView for assigned slot details
    - Add visual indicator for discrepancies
    - _Requirements: 10.2, 10.3, 10.4, 17.2_
  
  - [x] 23.3 Create SubjectDistributionAdapter.java
    - Display subject name and period counts
    - Display all assigned slots with day, period, class, faculty
    - Highlight discrepancies when assigned ≠ required
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 17.3_
  
  - [x] 23.4 Create SubjectDistributionActivity.java
    - Initialize TimetableViewModel
    - Set up RecyclerView with SubjectDistributionAdapter
    - Observe currentTimetable LiveData
    - Calculate and display subject distribution
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 15.1_

- [ ] 24. Write property tests for display and rendering
  - [ ] 24.1 Write property test for Cell Rendering Completeness
    - **Property 18: Cell Rendering Completeness**
    - Generate random timetables, verify rendered cells include subject, faculty, and room names
    - Tag: // Feature: timetable-generator, Property 18: Cell Rendering Completeness
    - **Validates: Requirements 8.2**
  
  - [ ] 24.2 Write property test for Faculty Schedule Rendering
    - **Property 19: Faculty Schedule Rendering**
    - Generate random faculty schedules, verify rendered display includes class, section, subject, room
    - Tag: // Feature: timetable-generator, Property 19: Faculty Schedule Rendering
    - **Validates: Requirements 9.2**
  
  - [ ] 24.3 Write property test for Faculty Workload Calculation
    - **Property 20: Faculty Workload Calculation**
    - Generate random faculty schedules, verify workload calculation equals period count
    - Tag: // Feature: timetable-generator, Property 20: Faculty Workload Calculation
    - **Validates: Requirements 9.3**

- [ ] 25. Implement manual timetable editing UI
  - [ ] 25.1 Create activity_edit_timetable.xml layout
    - Add RecyclerView for timetable grid (same as view mode)
    - Add "Lock Cell" button
    - Add "Swap Mode" toggle button
    - Add "Regenerate" button
    - Add conflict indicator area
    - _Requirements: 11.1, 11.7, 12.1, 17.1_
  
  - [ ] 25.2 Implement cell selection logic in EditTimetableActivity
    - Track selected cells for swapping
    - Highlight selected cells visually
    - Allow selecting two cells for swap operation
    - _Requirements: 11.3, 17.2_
  
  - [ ] 25.3 Create EditTimetableActivity.java
    - Initialize TimetableViewModel
    - Set up RecyclerView with TimetableGridAdapter
    - Implement lock cell functionality
    - Implement swap cell functionality (select two cells, then swap)
    - Observe editConflicts LiveData to display validation errors
    - Highlight conflicting cells in red when conflicts occur
    - Call viewModel.swapAssignments() and handle validation
    - Implement regenerate button that calls TimetableGenerator.regenerateWithLockedSlots()
    - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5, 11.6, 11.7, 12.1, 12.4, 15.1_
  
  - [ ] 25.4 Add conflict highlighting to TimetableGridAdapter
    - Accept list of conflicting slots
    - Apply red background to conflicting cells
    - _Requirements: 11.7, 13.4_

- [ ] 26. Write property tests for editing and regeneration
  - [ ]* 26.1 Write property test for Swap Constraint Validation
    - **Property 16: Swap Constraint Validation**
    - Generate random timetables and random swap operations, verify validation
    - Tag: // Feature: timetable-generator, Property 16: Swap Constraint Validation
    - **Validates: Requirements 11.4**
  
  - [ ]* 26.2 Write property test for Locked Slots Preservation
    - **Property 17: Locked Slots Preservation**
    - Generate random timetables with random locked slots, verify preservation after regeneration
    - Tag: // Feature: timetable-generator, Property 17: Locked Slots Preservation
    - **Validates: Requirements 12.1, 12.2**
  
  - [ ]* 26.3 Write property test for Conflict Report Completeness
    - **Property 21: Conflict Report Completeness**
    - Generate random constraint violations, verify conflict reports include constraint and entities
    - Tag: // Feature: timetable-generator, Property 21: Conflict Report Completeness
    - **Validates: Requirements 13.1, 13.2, 13.3**

- [ ] 27. Implement utility classes
  - [ ] 27.1 Create ValidationUtils.java
    - Implement validateConfig(Config) method
    - Implement validateSubject(Subject) method
    - Implement validateFaculty(Faculty) method
    - Return validation error messages
    - _Requirements: 1.6, 2.8, 3.7, 17.7_
  
  - [ ] 27.2 Create TimetableUtils.java
    - Implement helper methods for timetable operations
    - Implement method to calculate daily workload for faculty
    - Implement method to check if slot is adjacent to another
    - Implement method to format time slot display
    - _Requirements: 9.3, 18.2, 18.3_

- [ ] 28. Checkpoint - Test complete application flow
  - Run app from setup wizard through generation to viewing
  - Verify all screens navigate correctly
  - Verify timetable generation produces valid results
  - Verify manual editing works with constraint validation
  - Run all property tests to ensure correctness
  - Ask the user if questions arise
