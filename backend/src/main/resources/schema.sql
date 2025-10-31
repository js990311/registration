CREATE TABLE `lectures` (
    `lecture_id`	BIGINT	NOT NULL,
    `capacity`	INT	NOT NULL,
    `name`	VARCHAR(255)	NOT NULL
);

CREATE TABLE `students` (
    `student_id`	BIGINT	NOT NULL,
    `name`	VARCHAR(255)	NOT NULL
);

CREATE TABLE `registrations` (
    `registration_id`	BIGINT	NOT NULL,
    `lecture_id`	BIGINT	NOT NULL,
    `student_id`	BIGINT	NOT NULL
);

ALTER TABLE `lectures` ADD CONSTRAINT `PK_LECTURES` PRIMARY KEY (
    `lecture_id`
);

ALTER TABLE `students` ADD CONSTRAINT `PK_STUDENTS` PRIMARY KEY (
    `student_id`
);

ALTER TABLE `registrations` ADD CONSTRAINT `PK_REGISTRATIONS` PRIMARY KEY (
    `registration_id`
);

ALTER TABLE `registrations` ADD CONSTRAINT `FK_lectures_TO_registrations_1` FOREIGN KEY (
    `lecture_id`
)
REFERENCES `lectures` (
    `lecture_id`
);

ALTER TABLE `registrations` ADD CONSTRAINT `FK_students_TO_registrations_1` FOREIGN KEY (
    `student_id`
)
REFERENCES `students` (
    `student_id`
);

