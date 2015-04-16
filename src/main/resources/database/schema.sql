CREATE TABLE `job` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `title` varchar(64) NOT NULL,
    `description` varchar(256) DEFAULT '',
    `cron_expression` varchar(128) NOT NULL,
    `time_zone` varchar(32) NOT NULL,
    `timeout` int(16) DEFAULT 0,
    `retry` int(8) DEFAULT 0,
    `retry_interval` int(8) DEFAULT 0,
    `run_type` int(8) DEFAULT 0,
    `fail_strategy` int(8) DEFAULT 0,
    `job_group_name` varchar(64) NOT NULL,
    `status` int(8) DEFAULT 0,
    `run_as` varchar(64) DEFAULT '',
    `last_schedule_id` bigint(20),
    `created_date` date,
    `enable_date` date,
    `last_modified_by` varchar(64),
    `last_modified_date` date,
    `created_by` varchar(64),
    PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `job_group` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL,
    PRIMARY KEY(`id`),
    UNIQUE KEY `index_name`(`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `job_schedule` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_datetime` DATETIME NOT NULL,
    `schedule_datetime` DATETIME NOT NULL
    `time_zone` int(8) DEFAULT 0,
    `job_id` bigint(20) NOT NULL,
    `job_group_name` varchar(64) NOT NULL,
    `status` int(8) NOT NULL,
    `retried` int(8),
    `run_as` varchar(64),
    `next_job_schedule_id` bigint(20) DEFAULT 0,
    PRIMARY KEY(`id`),
    INDEX index_created_datetime(job_id, created_datetime)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `job_transaction` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `start_datetime` DATETIME NOT NULL,
    `end_datetime` DATETIME NOT NULL,
    `time_zone` int(8) DATETIME 0,
    `job_id` bigint(20) NOT NULL,
     `job_schedule_id` bigint(20) NOT NULL,
    `host` varchar(64),
    `pid` int(64),
    `run_as` varchar(64),
    `exit_code` int(8),
    `status` int(8) NOT NULL,
    PRIMARY KEY(`id`),
    INDEX index_id_starttime (job_schedule_id, start_datetime)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `job_script` (
    `job_id` bigint(20) NOT NULL,
    `script` text NOT NULL,
    PRIMARY KEY(`job_id`),
)ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `job_open_task` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `job_id` bigint(20) NOT NULL,
    `reference_id` bigint(20) NOT NULL,
    `type` int(8) NOT NULL,
    `created_datetime` DATETIME NOT NULL,
    PRIMARY KEY(`id`),
    INDEX index_job (job_id),
    INDEX index_reference (reference_id)
)ENGINE=InnoDB DEFAULT CHARSET=latin1;

