CREATE TABLE `job` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `title` varchar(64) NOT NULL,
    `description` varchar(256) NOT NULL,
    `cron_expression` varchar(128) NOT NULL,
    `time_zone` int(8) DEFAULT NULL,
    `timeout` int(16) DEFAULT 0,
    `retry` int(8) DEFAULT 0,
    `retry_interval` int(8) DEFAULT 0,
    `run_type` int(8) DEFAULT 0,
    `fail_strategy` int(8) DEFAULT 0,
    `job_group_id` bigint(20) NOT NULL,
    `status` int(8) NOT NULL,
    `run_as` varchar(64),
    `last_schedule_id` bigint(20),
    `created_date` date,
    `enable_date` date,
    `last_modified_by` varchar(64),
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
    `start_datetime` TIMESTAMP NOT NULL,
    `job_id` bigint(20) NOT NULL,
    `job_group_name` varchar(64) NOT NULL,
    `status` int(8) NOT NULL,
    `retried` int(8),
    `run_as` varchar(64),
    PRIMARY KEY(`id`),
    INDEX index_id_starttime (job_id, start_datetime)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `job_transaction` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `start_datetime` TIMESTAMP NOT NULL,
    `end_datetime` TIMESTAMP NOT NULL,
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

