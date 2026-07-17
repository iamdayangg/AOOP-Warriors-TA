-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 24, 2026 at 12:43 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `motorph_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

CREATE TABLE `attendance` (
  `attendance_id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `attendance_date` date NOT NULL,
  `log_in_time` time NOT NULL,
  `log_out_time` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `attendance`
--

INSERT INTO `attendance` (`attendance_id`, `employee_id`, `attendance_date`, `log_in_time`, `log_out_time`) VALUES
(180, 10005, '2026-06-19', '12:35:00', '12:36:00'),
(181, 10003, '2025-01-08', '08:10:00', '17:00:00'),
(182, 10001, '2025-01-08', '07:58:00', '17:00:00'),
(183, 10009, '2025-01-08', '08:05:00', '17:00:00'),
(184, 10002, '2025-01-08', '08:00:00', '17:05:00'),
(185, 10008, '2025-01-07', '08:20:00', '17:00:00'),
(186, 10001, '2025-01-07', '08:05:00', '17:00:00'),
(187, 10009, '2025-01-07', '08:00:00', '17:00:00'),
(188, 10003, '2025-01-07', '08:00:00', '17:00:00'),
(189, 10002, '2025-01-07', '08:15:00', '17:00:00'),
(190, 10002, '2025-01-06', '08:00:00', '17:00:00'),
(191, 10009, '2025-01-06', '08:00:00', '17:00:00'),
(192, 10001, '2025-01-06', '08:00:00', '17:00:00'),
(193, 10003, '2025-01-06', '08:00:00', '17:00:00'),
(194, 10001, '2024-06-03', '08:59:00', '18:31:00'),
(195, 10002, '2024-06-03', '10:35:00', '19:44:00'),
(197, 10004, '2024-06-03', '10:57:00', '18:14:00'),
(198, 10005, '2024-06-03', '09:48:00', '17:13:00'),
(199, 10006, '2024-06-03', '09:31:00', '19:29:00'),
(200, 10007, '2024-06-03', '09:09:00', '16:30:00'),
(201, 10008, '2024-06-03', '09:02:00', '18:06:00'),
(202, 10009, '2024-06-03', '08:18:00', '17:40:00'),
(203, 10010, '2024-06-03', '08:10:00', '15:13:00'),
(204, 10011, '2024-06-03', '09:08:00', '19:36:00'),
(205, 10012, '2024-06-03', '09:47:00', '18:43:00'),
(206, 10013, '2024-06-03', '09:48:00', '19:21:00'),
(207, 10014, '2024-06-03', '09:23:00', '18:09:00'),
(208, 10015, '2024-06-03', '08:41:00', '19:27:00'),
(209, 10016, '2024-06-03', '08:41:00', '16:45:00'),
(210, 10017, '2024-06-03', '09:40:00', '17:24:00'),
(211, 10018, '2024-06-03', '08:22:00', '16:46:00'),
(212, 10019, '2024-06-03', '09:53:00', '17:24:00'),
(213, 10020, '2024-06-03', '08:47:00', '16:27:00'),
(214, 10021, '2024-06-03', '09:37:00', '18:45:00'),
(215, 10022, '2024-06-03', '10:54:00', '20:10:00'),
(216, 10023, '2024-06-03', '10:27:00', '20:10:00'),
(217, 10024, '2024-06-03', '09:16:00', '17:57:00'),
(218, 10025, '2024-06-03', '10:18:00', '18:07:00'),
(219, 10026, '2024-06-03', '08:17:00', '18:31:00'),
(221, 10028, '2024-06-03', '08:52:00', '17:23:00'),
(222, 10029, '2024-06-03', '10:57:00', '21:44:00'),
(223, 10030, '2024-06-03', '09:04:00', '16:39:00'),
(224, 10031, '2024-06-03', '10:07:00', '20:51:00'),
(225, 10032, '2024-06-03', '08:29:00', '16:46:00'),
(226, 10033, '2024-06-03', '10:02:00', '19:39:00'),
(227, 10034, '2024-06-03', '10:05:00', '18:12:00'),
(228, 10001, '2024-06-04', '09:47:00', '19:07:00'),
(229, 10002, '2024-06-04', '10:11:00', '20:16:00'),
(230, 10003, '2024-06-04', '10:45:00', '20:37:00'),
(231, 10004, '2024-06-04', '09:45:00', '16:54:00'),
(232, 10005, '2024-06-04', '09:50:00', '20:20:00'),
(233, 10006, '2024-06-04', '10:00:00', '20:12:00'),
(234, 10007, '2024-06-04', '09:16:00', '16:18:00'),
(235, 10008, '2024-06-04', '09:36:00', '18:56:00'),
(236, 10009, '2024-06-04', '10:41:00', '19:21:00'),
(237, 10010, '2024-06-04', '08:05:00', '18:36:00'),
(238, 10011, '2024-06-04', '09:58:00', '17:04:00'),
(239, 10012, '2024-06-04', '08:41:00', '16:55:00'),
(240, 10013, '2024-06-04', '08:13:00', '17:58:00'),
(241, 10014, '2024-06-04', '09:11:00', '17:10:00'),
(242, 10015, '2024-06-04', '10:35:00', '18:43:00'),
(243, 10016, '2024-06-04', '10:15:00', '18:12:00'),
(244, 10017, '2024-06-04', '09:04:00', '17:32:00'),
(245, 10018, '2024-06-04', '10:40:00', '21:01:00'),
(246, 10019, '2024-06-04', '08:05:00', '18:10:00'),
(247, 10020, '2024-06-04', '09:05:00', '18:27:00'),
(248, 10021, '2024-06-04', '08:59:00', '19:50:00'),
(249, 10022, '2024-06-04', '10:58:00', '18:51:00'),
(250, 10023, '2024-06-04', '10:06:00', '20:32:00'),
(251, 10024, '2024-06-04', '08:35:00', '18:14:00'),
(252, 10025, '2024-06-04', '08:01:00', '15:39:00'),
(253, 10026, '2024-06-04', '08:25:00', '16:41:00'),
(254, 10027, '2024-06-04', '10:47:00', '20:39:00'),
(255, 10028, '2024-06-04', '10:05:00', '19:33:00'),
(256, 10029, '2024-06-04', '10:38:00', '20:56:00'),
(257, 10030, '2024-06-04', '08:20:00', '16:30:00'),
(258, 10031, '2024-06-04', '10:13:00', '17:52:00'),
(259, 10032, '2024-06-04', '08:48:00', '16:36:00'),
(260, 10033, '2024-06-04', '10:10:00', '18:54:00'),
(261, 10000, '2024-06-04', '09:35:00', '19:15:00'),
(262, 10000, '2026-02-17', '08:00:00', '17:00:00'),
(263, 10000, '2026-02-18', '08:00:00', '17:00:00'),
(265, 10000, '2026-02-20', '08:00:00', '17:00:00'),
(266, 10000, '2026-02-21', '08:00:00', '17:00:00'),
(267, 10000, '2026-02-24', '08:00:00', '17:00:00'),
(269, 10000, '2026-02-26', '08:00:00', '17:00:00'),
(270, 10000, '2026-02-27', '08:00:00', '17:00:00'),
(271, 10000, '2026-02-28', '08:00:00', '17:00:00'),
(272, 10000, '2026-01-02', '08:00:00', '17:00:00'),
(273, 10000, '2026-01-05', '08:00:00', '17:00:00'),
(274, 10000, '2026-01-06', '08:00:00', '17:00:00'),
(275, 10000, '2026-01-07', '08:00:00', '17:00:00'),
(276, 10000, '2026-01-08', '08:00:00', '17:00:00'),
(277, 10000, '2026-01-09', '08:00:00', '17:00:00'),
(278, 10000, '2026-01-12', '08:00:00', '17:00:00'),
(279, 10000, '2026-01-13', '08:00:00', '17:00:00'),
(280, 10000, '2026-01-14', '08:00:00', '17:00:00'),
(281, 10000, '2026-01-15', '08:00:00', '17:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `benefits`
--

CREATE TABLE `benefits` (
  `benefit_id` int(11) NOT NULL,
  `benefit_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `benefits`
--

INSERT INTO `benefits` (`benefit_id`, `benefit_name`) VALUES
(3, 'Clothing Allowance'),
(2, 'Phone Allowance'),
(1, 'Rice Subsidy');

-- --------------------------------------------------------

--
-- Table structure for table `compensation`
--

CREATE TABLE `compensation` (
  `compensation_id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `basic_salary` decimal(12,2) NOT NULL,
  `semi_monthly_rate` decimal(12,2) NOT NULL,
  `hourly_rate` decimal(10,4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `compensation`
--

INSERT INTO `compensation` (`compensation_id`, `employee_id`, `basic_salary`, `semi_monthly_rate`, `hourly_rate`) VALUES
(1, 10001, 90000.00, 45000.00, 535.7100),
(2, 10002, 60000.00, 30000.00, 357.1400),
(3, 10003, 60000.00, 30000.00, 357.1400),
(4, 10004, 60000.00, 30000.00, 357.1400),
(5, 10005, 52670.00, 26335.00, 313.5100),
(6, 10006, 52670.00, 26335.00, 313.5100),
(7, 10007, 42975.00, 21488.00, 255.8000),
(8, 10008, 22500.00, 11250.00, 133.9300),
(9, 10009, 22500.00, 11250.00, 133.9300),
(10, 10010, 52670.00, 26335.00, 313.5100),
(11, 10011, 50825.00, 25413.00, 302.5300),
(12, 10012, 38475.00, 19238.00, 229.0200),
(13, 10013, 24000.00, 12000.00, 142.8600),
(14, 10014, 24000.00, 12000.00, 142.8600),
(15, 10015, 53500.00, 26750.00, 318.4500),
(16, 10016, 42975.00, 21488.00, 255.8000),
(17, 10017, 41850.00, 20925.00, 249.1100),
(18, 10018, 22500.00, 11250.00, 133.9300),
(19, 10019, 22500.00, 11250.00, 133.9300),
(20, 10020, 23250.00, 11625.00, 138.3900),
(21, 10021, 23250.00, 11625.00, 138.3900),
(22, 10022, 24000.00, 12000.00, 142.8600),
(23, 10023, 22500.00, 11250.00, 133.9300),
(24, 10024, 22500.00, 11250.00, 133.9300),
(25, 10025, 24000.00, 12000.00, 142.8600),
(26, 10026, 24750.00, 12375.00, 147.3200),
(27, 10027, 24750.00, 12375.00, 147.3200),
(28, 10028, 24000.00, 12000.00, 142.8600),
(29, 10029, 22500.00, 11250.00, 133.9300),
(30, 10030, 22500.00, 11250.00, 133.9300),
(31, 10031, 22500.00, 11250.00, 133.9300),
(32, 10032, 52670.00, 26335.00, 313.5100),
(33, 10033, 52670.00, 26335.00, 313.5100),
(34, 10034, 52670.00, 26335.00, 313.5100),
(35, 10000, 90000.00, 45000.00, 1000.0000);

-- --------------------------------------------------------

--
-- Table structure for table `compensation_details`
--

CREATE TABLE `compensation_details` (
  `compensation_detail_id` int(11) NOT NULL,
  `compensation_id` int(11) NOT NULL,
  `benefit_id` int(11) NOT NULL,
  `amount` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `compensation_details`
--

INSERT INTO `compensation_details` (`compensation_detail_id`, `compensation_id`, `benefit_id`, `amount`) VALUES
(1, 1, 1, 1500.00),
(2, 1, 2, 2000.00),
(3, 1, 3, 1000.00),
(4, 2, 1, 1500.00),
(5, 2, 2, 2000.00),
(6, 2, 3, 1000.00),
(7, 3, 1, 1500.00),
(8, 3, 2, 2000.00),
(9, 3, 3, 1000.00),
(10, 4, 1, 1500.00),
(11, 4, 2, 2000.00),
(12, 4, 3, 1000.00),
(13, 5, 1, 1500.00),
(14, 5, 2, 1000.00),
(15, 5, 3, 1000.00),
(16, 6, 1, 1500.00),
(17, 6, 2, 1000.00),
(18, 6, 3, 1000.00),
(19, 7, 1, 1500.00),
(20, 7, 2, 800.00),
(21, 7, 3, 800.00),
(22, 8, 1, 1500.00),
(23, 8, 2, 500.00),
(24, 8, 3, 500.00),
(25, 9, 1, 1500.00),
(26, 9, 2, 500.00),
(27, 9, 3, 500.00),
(28, 10, 1, 1500.00),
(29, 10, 2, 1000.00),
(30, 10, 3, 1000.00),
(31, 11, 1, 1500.00),
(32, 11, 2, 1000.00),
(33, 11, 3, 1000.00),
(34, 12, 1, 1500.00),
(35, 12, 2, 800.00),
(36, 12, 3, 800.00),
(37, 13, 1, 1500.00),
(38, 13, 2, 500.00),
(39, 13, 3, 500.00),
(40, 14, 1, 1500.00),
(41, 14, 2, 500.00),
(42, 14, 3, 500.00),
(43, 15, 1, 1500.00),
(44, 15, 2, 1000.00),
(45, 15, 3, 1000.00),
(46, 16, 1, 1500.00),
(47, 16, 2, 800.00),
(48, 16, 3, 800.00),
(49, 17, 1, 1500.00),
(50, 17, 2, 800.00),
(51, 17, 3, 800.00),
(52, 18, 1, 1500.00),
(53, 18, 2, 500.00),
(54, 18, 3, 500.00),
(55, 19, 1, 1500.00),
(56, 19, 2, 500.00),
(57, 19, 3, 500.00),
(58, 20, 1, 1500.00),
(59, 20, 2, 500.00),
(60, 20, 3, 500.00),
(61, 21, 1, 1500.00),
(62, 21, 2, 500.00),
(63, 21, 3, 500.00),
(64, 22, 1, 1500.00),
(65, 22, 2, 500.00),
(66, 22, 3, 500.00),
(67, 23, 1, 1500.00),
(68, 23, 2, 500.00),
(69, 23, 3, 500.00),
(70, 24, 1, 1500.00),
(71, 24, 2, 500.00),
(72, 24, 3, 500.00),
(73, 25, 1, 1500.00),
(74, 25, 2, 500.00),
(75, 25, 3, 500.00),
(76, 26, 1, 1500.00),
(77, 26, 2, 500.00),
(78, 26, 3, 500.00),
(79, 27, 1, 1500.00),
(80, 27, 2, 500.00),
(81, 27, 3, 500.00),
(82, 28, 1, 1500.00),
(83, 28, 2, 500.00),
(84, 28, 3, 500.00),
(85, 29, 1, 1500.00),
(86, 29, 2, 500.00),
(87, 29, 3, 500.00),
(88, 30, 1, 1500.00),
(89, 30, 2, 500.00),
(90, 30, 3, 500.00),
(91, 31, 1, 1500.00),
(92, 31, 2, 500.00),
(93, 31, 3, 500.00),
(94, 32, 1, 1500.00),
(95, 32, 2, 1000.00),
(96, 32, 3, 1000.00),
(97, 33, 1, 1500.00),
(98, 33, 2, 1000.00),
(99, 33, 3, 1000.00),
(100, 34, 1, 1500.00),
(101, 34, 2, 1000.00),
(102, 34, 3, 1000.00),
(103, 35, 1, 1500.00),
(104, 35, 2, 1500.00),
(105, 35, 3, 1500.00);

-- --------------------------------------------------------

--
-- Table structure for table `credentials`
--

CREATE TABLE `credentials` (
  `employee_id` int(11) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `credentials`
--

INSERT INTO `credentials` (`employee_id`, `password`, `role_id`) VALUES
(10000, 'pbkdf2$120000$MZnEr+MDQGkpzNcGYN5ohA==$8sn1ARYkASlxHQyor59OCTOvVFNhjT5GbVpdn4xFbHE=', 1),
(10001, 'pbkdf2$120000$IsDlpg0oOT6kIRbL8B/e6A==$bGQwEwxcE4YfYZ7esKhAkoIrGaSfKuNu4+0qTBfxxm8=', 2),
(10002, 'pbkdf2$120000$tzQv8o2O3Lj/2vMgK25tag==$HSRJNFqlM2M5njCI+iEXt4bGV6+9P/2TLJ6IJx7jiZI=', 3),
(10003, 'pbkdf2$120000$6NrK9iczC+ISQWGprI3RXA==$JmuPNBf+0ztHEd7DNioymRUUYTmxiTjvlTQNCGSNkW4=', 3),
(10004, 'pbkdf2$120000$ErHCsYvDF2MkIuay/j3Hfg==$Jd8qTcwNirTLUPCUHY9+GEpthxiV2E6KgpJyW/XSYG0=', 4),
(10005, 'pbkdf2$120000$Zygm/07cHNRr54ZKeHAdJQ==$4jfcB7EOypkDAHs49oRXMIEswxFurHHf54h5SrhZaoA=', 5),
(10006, 'pbkdf2$120000$4gPWVI70jVUIc3nGeS3Gkw==$bvjHBliF9EcYG69TTJhWp+aX+e/BCnxqVVvMRsIXwDs=', 2),
(10007, 'pbkdf2$120000$mD2pb5uGTzfLo520kpAbYA==$g/WXR2ON+D9VjDiecF4O2YYA7IVLTCVYEjVAfn9QU+k=', 2),
(10008, 'pbkdf2$120000$W811awocdnpjT8vIZmuLjA==$+v9vFZwUwbo5urklAs4Z2zpP8z6TPd6YBUdK5kv8G6Q=', 4),
(10009, 'pbkdf2$120000$YskoHpaVAxcudiKv6hLglA==$o147m9UXRjQOj6DoK4qXxCysL/ELIQPpmFwiB1z9wEw=', 4),
(10010, 'pbkdf2$120000$2a//tjoHvZll6Jb683hFNw==$91WeRNZeMmWdbKDM3dglDundUZNREBDzjfyWluhOoAg=', 3),
(10011, 'pbkdf2$120000$o/WD+GBjFlPH83y/TUNlIA==$4GJuZIw7piP0EELahnLqHcoQSWI0a5iS3Ofd3ngQgic=', 3),
(10012, 'pbkdf2$120000$gJD5h0RHgMf+rXmtg33vug==$AysjYIn6vpuoYsYLs79j6qDv4LHyPh8iIqFMGknCfUw=', 4),
(10013, 'pbkdf2$120000$EBmrDOxh8+QihmnCQturcw==$NsTOBNMVdPvpeTynOVZYqp26hLRErp+Z/fcE/Un7JVg=', 4),
(10014, 'pbkdf2$120000$r8JfzcFWUV1Vu8DsxXycPg==$yNm68OD9leYX9/jM82iBKO5ruCk+Czj6qG8lm2AU15k=', 4),
(10015, 'pbkdf2$120000$u74cd1rILC8DWNUYp/mL9w==$uJSzuMNrE4k1BW6VNt83MD8gdRUmai/PfND6c8AsHvY=', 4),
(10016, 'pbkdf2$120000$wdpA7gu9RglLodowVJ7r0Q==$C7nB62Jf44UIR5NLNww4yWMpMBYh6qeo5LDoZIPnW3E=', 4),
(10017, 'pbkdf2$120000$FRFX8XrCTqUNB/Oapm30dw==$t9W40UU8TkcbxiLKKZ8hKiPV10wf/Pcxm4C+2lCvOvI=', 4),
(10018, 'pbkdf2$120000$PCTMXdDTIzrM32U6m76b7A==$3K2P4ep4teO4VjqkfMk1yzvZCFfGyjUkuyJvBG8moHI=', 4),
(10019, 'pbkdf2$120000$Z46E4HUjyy6VCmUiWRkgOQ==$i/Qgae/z8gproatiEgfp6MU0ygGBO9PYcdRVJOfwR2c=', 4),
(10020, 'pbkdf2$120000$9xuMQaIxojDKvc/phDhtlA==$fsBN9QyiPUd2sZguZOjp/VdxH47/1H4qXIybONOLOJM=', 4),
(10021, 'pbkdf2$120000$H9uZY+JdDcKMkwioSbIjrA==$15qAVcK09zfRd7/rZlobeUMyPGtuZp/ZmZcp7xkOhME=', 4),
(10022, 'pbkdf2$120000$r/e6ZSTZaIpvYgz8Y1ytMQ==$ADdDcHXQmNTzVbsc5Ci25GC5AzcBNdyIxiRNbb40bsE=', 4),
(10023, 'pbkdf2$120000$bCHF3zwqE5LhlNyONPD1Rw==$kkSOjPr/So7ZZjFoKQPfZzyXasnMQNVMV2C68ZpJMCU=', 4),
(10024, 'pbkdf2$120000$CbbPnhocg8Bac5TxbQN5Eg==$szWHwAhNgkrzrI68MP4Ub6tR6XlrA+4DcywuzqK0FeY=', 4),
(10025, 'pbkdf2$120000$ta53+JllVJFIOdB7EgPzxQ==$l1zCboDnlT+VDCHrAqVTyNJ5qzCPb7CKd1MISaUn5AQ=', 4),
(10026, 'pbkdf2$120000$Rjf+A+9MONBctZaOFY+N5Q==$yQNmZ02kLli1QL4eNZI85JGeW5K/c7NTkpdq+RbqJnM=', 4),
(10027, 'pbkdf2$120000$7UiFqRDfHoL3rjxrlsVFyQ==$Pguf/YmdAFbRy1D9KsydyVlkR/HB/YJuqAwOes3sfiI=', 4),
(10028, 'pbkdf2$120000$hhapRxklBMb3+XVGEDnD6A==$BGYgaTvRZ5xomXmqdGl5H2hoTzUwrBb6eKj9JaJ48BQ=', 4),
(10029, 'pbkdf2$120000$N09hT7EEzuDVyy1y6RWckw==$vCgSixATWpkvhLfw/yXcO/6is4hOjJEudgwICQwvitU=', 4),
(10030, 'pbkdf2$120000$tuMRhgydrqqf/MlV3NTb9w==$LNMzMmQ/ctreAkNyw0LRV19S0O3/tt5q5Mtxj9ZcBk0=', 4),
(10031, 'pbkdf2$120000$YuYfNkUWSKaMUR5w87xeBw==$MuPauDprn031K29ngbhA7CtvoE+EisJPcGH8gy5tJhE=', 4),
(10032, 'pbkdf2$120000$tnt05HUHq+kH5bNw7od8Ww==$42JsbYeqMVrD90VcoW5n2z6VTFcpC1ywxleOZtmvUS8=', 4),
(10033, 'pbkdf2$120000$x7hATSJD4s7ucB1W5sTLVQ==$shooIYjOlK+sYgjyZbb9EbRPE8YEtKs07wqu1x6uJI0=', 4),
(10034, 'pbkdf2$120000$Lmhy2iUx73uqprlSchVwpw==$URuerZCfXk5GOB99bYTv39+ztNRMhWKHpB/olDaai40=', 4);

-- --------------------------------------------------------

--
-- Table structure for table `deduction`
--

CREATE TABLE `deduction` (
  `deduction_id` int(11) NOT NULL,
  `payroll_id` int(11) NOT NULL,
  `deduction_type_id` int(11) NOT NULL,
  `amount` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `deduction`
--

INSERT INTO `deduction` (`deduction_id`, `payroll_id`, `deduction_type_id`, `amount`) VALUES
(65, 8, 1, 1125.00),
(66, 8, 2, 450.00),
(67, 8, 3, 100.00),
(68, 8, 4, 7100.00),
(69, 13, 1, 900.00),
(70, 13, 2, 337.50),
(71, 13, 3, 100.00),
(72, 13, 4, 856.25),
(73, 14, 1, 1125.00),
(74, 14, 2, 450.00),
(75, 14, 3, 100.00),
(76, 14, 4, 7100.00),
(77, 15, 1, 1125.00),
(78, 15, 2, 450.00),
(79, 15, 3, 100.00),
(80, 15, 4, 3460.33),
(81, 16, 1, 1125.00),
(82, 16, 2, 450.00),
(83, 16, 3, 100.00),
(84, 16, 4, 2515.16),
(85, 17, 1, 900.00),
(86, 17, 2, 337.50),
(87, 17, 3, 100.00),
(88, 17, 4, 856.25),
(93, 19, 1, 1125.00),
(94, 19, 2, 450.00),
(95, 19, 3, 100.00),
(96, 19, 4, 3460.33),
(97, 20, 1, 1125.00),
(98, 20, 2, 450.00),
(99, 20, 3, 100.00),
(100, 20, 4, 7100.00),
(101, 21, 1, 1125.00),
(102, 21, 2, 450.00),
(103, 21, 3, 100.00),
(104, 21, 4, 7100.00),
(105, 22, 1, 1125.00),
(106, 22, 2, 450.00),
(107, 22, 3, 100.00),
(108, 22, 4, 4175.00),
(109, 23, 1, 1125.00),
(110, 23, 2, 450.00),
(111, 23, 3, 100.00),
(112, 23, 4, 4175.00),
(113, 24, 1, 1125.00),
(114, 24, 2, 450.00),
(115, 24, 3, 100.00),
(116, 24, 4, 4175.00),
(117, 25, 1, 1125.00),
(118, 25, 2, 450.00),
(119, 25, 3, 100.00),
(120, 25, 4, 4175.00),
(121, 26, 1, 1125.00),
(122, 26, 2, 450.00),
(123, 26, 3, 100.00),
(124, 26, 4, 4175.00),
(125, 27, 1, 1125.00),
(126, 27, 2, 450.00),
(127, 27, 3, 100.00),
(128, 27, 4, 4175.00),
(129, 28, 1, 1125.00),
(130, 28, 2, 450.00),
(131, 28, 3, 100.00),
(132, 28, 4, 3460.33),
(133, 29, 1, 1125.00),
(134, 29, 2, 450.00),
(135, 29, 3, 100.00),
(136, 29, 4, 3460.33),
(137, 30, 1, 1125.00),
(138, 30, 2, 450.00),
(139, 30, 3, 100.00),
(140, 30, 4, 3460.33),
(141, 31, 1, 1125.00),
(142, 31, 2, 450.00),
(143, 31, 3, 100.00),
(144, 31, 4, 3460.33),
(145, 32, 1, 1125.00),
(146, 32, 2, 450.00),
(147, 32, 3, 100.00),
(148, 32, 4, 2515.16),
(149, 33, 1, 1125.00),
(150, 33, 2, 450.00),
(151, 33, 3, 100.00),
(152, 33, 4, 2515.16),
(153, 34, 1, 900.00),
(154, 34, 2, 337.50),
(155, 34, 3, 100.00),
(156, 34, 4, 856.25),
(157, 35, 1, 900.00),
(158, 35, 2, 337.50),
(159, 35, 3, 100.00),
(160, 35, 4, 856.25),
(161, 36, 1, 900.00),
(162, 36, 2, 337.50),
(163, 36, 3, 100.00),
(164, 36, 4, 856.25);

-- --------------------------------------------------------

--
-- Table structure for table `deduction_types`
--

CREATE TABLE `deduction_types` (
  `deduction_type_id` int(11) NOT NULL,
  `deduction_name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `deduction_types`
--

INSERT INTO `deduction_types` (`deduction_type_id`, `deduction_name`, `description`) VALUES
(1, 'SSS', 'Social Security System contribution'),
(2, 'PhilHealth', 'Philippine Health Insurance Corporation contribution'),
(3, 'Pag-ibig', 'Home Development Mutual Fund contribution'),
(4, 'Withholding Tax', 'Bureau of Internal Revenue withholding tax');

-- --------------------------------------------------------

--
-- Table structure for table `employees`
--

CREATE TABLE `employees` (
  `employee_id` int(11) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `birthday` date NOT NULL,
  `address` varchar(255) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `emp_status` enum('Regular','Probationary') NOT NULL DEFAULT 'Regular',
  `position` varchar(100) NOT NULL,
  `department` varchar(100) NOT NULL,
  `supervisor_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `employees`
--

INSERT INTO `employees` (`employee_id`, `last_name`, `first_name`, `gender`, `birthday`, `address`, `phone_number`, `emp_status`, `position`, `department`, `supervisor_id`) VALUES
(10000, 'Ababao', 'Diane', 'Female', '1990-02-17', 'Sipalay City', '895-716-8380', 'Regular', 'Co-founder', 'Executive', NULL),
(10001, 'Garcia', 'Manuel III', 'Male', '1983-10-11', 'Valero Carpark Building Valero Street 1227, Makati City', '966-860-270', 'Regular', 'Chief Executive Officer', 'Executive', 10000),
(10002, 'Lim', 'Antonio', 'Male', '1988-06-19', 'San Antonio De Padua 2, Block 1 Lot 8 and 2, Dasmarinas, Cavite', '171-867-411', 'Regular', 'Chief Operating Officer', 'Operations', 10001),
(10003, 'Aquino', 'Bianca Sofia', 'Female', '1989-08-04', 'Rm. 402 4/F Jiao Building Timog Avenue Cor. Quezon Avenue 1100, Quezon City', '966-889-370', 'Regular', 'Chief Finance Officer', 'Finance', 10001),
(10004, 'Reyes', 'Isabella', 'Female', '1994-06-16', '460 Solanda Street Intramuros 1000, Manila', '786-868-477', 'Regular', 'Chief Marketing Officer', 'Marketing', 10001),
(10005, 'Hernandez', 'Eduard', 'Male', '1989-09-23', 'National Highway, Gingoog, Misamis Occidental', '088-861-012', 'Regular', 'IT Operations and Systems', 'IT', 10002),
(10006, 'Villanueva', 'Andrea Mae', 'Female', '1988-02-14', '17/85 Stracke Via Suite 042, Poblacion, Las Piñas 4783 Dinagat Islands', '918-621-603', 'Regular', 'HR Manager', 'HR', 10002),
(10007, 'San Jose', 'Brad', 'Male', '1996-03-15', '99 Strosin Hills, Poblacion, Bislig 5340 Tawi-Tawi', '797-009-261', 'Regular', 'HR Team Leader', 'HR', 10006),
(10008, 'Romualdez', 'Alice', 'Female', '1992-05-14', '12A/33 Upton Isle Apt. 420, Roxas City 1814 Surigao del Norte', '983-606-799', 'Regular', 'HR Rank and File', 'HR', 10007),
(10009, 'Atienza', 'Rosie', 'Female', '1948-09-24', '90A Dibbert Terrace Apt. 190, San Lorenzo 6056 Davao del Norte', '266-036-427', 'Regular', 'HR Rank and File', 'HR', 10007),
(10010, 'Alvaro', 'Roderick', 'Male', '1988-03-30', '#284 T. Morato corner, Scout Rallos Street, Quezon City', '053-381-386', 'Regular', 'Accounting Head', 'Finance', 10003),
(10011, 'Salcedo', 'Anthony', 'Male', '1993-09-14', '93/54 Shanahan Alley Apt. 183, Santo Tomas 1572 Masbate', '070-766-300', 'Regular', 'Payroll Manager', 'Finance', 10010),
(10012, 'Lopez', 'Josie', 'Female', '1987-01-14', '49 Springs Apt. 266, Poblacion, Taguig 3200 Occidental Mindoro', '478-355-427', 'Regular', 'Payroll Team Leader', 'Finance', 10011),
(10013, 'Farala', 'Martha', 'Female', '1942-01-11', '42/25 Sawayn Stream, Ubay 1208 Zamboanga del Norte', '329-034-366', 'Regular', 'Payroll Rank and File', 'Finance', 10011),
(10014, 'Martinez', 'Leila', 'Female', '1970-07-11', '37/46 Kulas Roads, Maragondon 0962 Quirino', '877-110-749', 'Regular', 'Payroll Rank and File', 'Finance', 10011),
(10015, 'Romualdez', 'Fredrick', 'Male', '1985-03-10', '22A/52 Lubowitz Meadows, Pililla 4895 Zambales', '023-079-009', 'Regular', 'Account Manager', 'Operations', 10002),
(10016, 'Mata', 'Christian', 'Male', '1987-10-21', '90 O\'Keefe Spur Apt. 379, Catigbian 2772 Sulu', '783-776-744', 'Regular', 'Account Team Leader', 'Operations', 10015),
(10017, 'De Leon', 'Selena', 'Female', '1975-02-20', '89A Armstrong Trace, Compostela 7874 Maguindanao', '975-432-139', 'Regular', 'Account Team Leader', 'Operations', 10015),
(10018, 'San Jose', 'Allison', 'Female', '1986-06-24', '08 Grant Drive Suite 406, Poblacion, Iloilo City 9186 La Union', '179-075-129', 'Regular', 'Account Rank and File', 'Operations', 10016),
(10019, 'Rosario', 'Cydney', 'Female', '1996-10-06', '93A/21 Berge Points, Tapaz 2180 Quezon', '868-819-912', 'Regular', 'Account Rank and File', 'Operations', 10016),
(10020, 'Bautista', 'Mark', 'Male', '1991-02-12', '65 Murphy Center Suite 094, Poblacion, Palayan 5636 Quirino', '683-725-348', 'Regular', 'Account Rank and File', 'Operations', 10016),
(10021, 'Lazaro', 'Darlene', 'Female', '1985-11-25', '47A/94 Larkin Plaza Apt. 179, Poblacion, Caloocan 2751 Quirino', '740-721-558', 'Probationary', 'Account Rank and File', 'Operations', 10016),
(10022, 'Delos Santos', 'Kolby', 'Male', '1980-02-26', '06A Gulgowski Extensions, Bongabon 6085 Zamboanga del Sur', '739-443-033', 'Probationary', 'Account Rank and File', 'Operations', 10016),
(10023, 'Santos', 'Vella', NULL, '1983-12-31', '99A Padberg Spring, Poblacion, Mabalacat 3959 Lanao del Sur', '955-879-269', 'Probationary', 'Account Rank and File', 'Operations', 10016),
(10024, 'Del Rosario', 'Tomas', 'Male', '1978-12-18', '80A/48 Ledner Ridges, Poblacion, Kabankalan 8870 Marinduque', '882-550-989', 'Probationary', 'Account Rank and File', 'Operations', 10016),
(10025, 'Tolentino', 'Jacklyn', NULL, '1984-05-19', '96/48 Watsica Flats Suite 734, Poblacion, Malolos 1844 Ifugao', '675-757-366', 'Probationary', 'Account Rank and File', 'Operations', 10017),
(10026, 'Gutierrez', 'Percival', 'Male', '1970-12-18', '58A Wilderman Walks, Poblacion, Digos 5822 Davao del Sur', '512-899-876', 'Probationary', 'Account Rank and File', 'Operations', 10017),
(10027, 'Manalaysay', 'Garfield', 'Male', '1986-08-28', '60 Goyette Valley Suite 219, Poblacion, Tabuk 3159 Lanao del Sur', '948-628-136', 'Probationary', 'Account Rank and File', 'Operations', 10017),
(10028, 'Villegas', 'Lizeth', 'Female', '1981-12-12', '66/77 Mann Views, Luisiana 1263 Dinagat Islands', '332-372-215', 'Probationary', 'Account Rank and File', 'Operations', 10017),
(10029, 'Ramos', 'Carol', 'Female', '1978-08-20', '72/70 Stamm Spurs, Bustos 4550 Iloilo', '250-700-389', 'Probationary', 'Account Rank and File', 'Operations', 10017),
(10030, 'Maceda', 'Emelia', 'Female', '1973-04-14', '50A/83 Bahringer Oval Suite 145, Kiamba 7688 Nueva Ecija', '973-358-041', 'Probationary', 'Account Rank and File', 'Operations', 10017),
(10031, 'Aguilar', 'Delia', 'Female', '1989-01-27', '95 Cremin Junction, Surallah 2809 Cotabato', '529-705-439', 'Probationary', 'Account Rank and File', 'Operations', 10017),
(10032, 'Castro', 'John Rafael', 'Male', '1992-02-09', 'Hi-way, Yati, Liloan Cebu', '332-424-955', 'Regular', 'Sales & Marketing', 'Marketing', 10004),
(10033, 'Martinez', 'Carlos Ian', 'Male', '1990-11-16', 'Bulala, Camalaniugan', '078-854-208', 'Regular', 'Supply Chain and Logistics', 'Operations', 10004),
(10034, 'Santos', 'Beatriz', 'Female', '1990-08-07', 'Agapita Building, Metro Manila', '526-639-511', 'Regular', 'Customer Service and Relations', 'Marketing', 10004);

-- --------------------------------------------------------

--
-- Table structure for table `employee_government_ids`
--

CREATE TABLE `employee_government_ids` (
  `gov_id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `sss_number` varchar(20) NOT NULL,
  `philhealth_number` varchar(20) NOT NULL,
  `tin_number` varchar(20) NOT NULL,
  `pagibig_number` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `employee_government_ids`
--

INSERT INTO `employee_government_ids` (`gov_id`, `employee_id`, `sss_number`, `philhealth_number`, `tin_number`, `pagibig_number`) VALUES
(1, 10000, '84-4506057-3', '720126853900', '442-605-657-100', '691295330800'),
(2, 10001, '44-4506057-3', '820126853951', '442-605-657-000', '691295330870'),
(3, 10002, '52-2061274-9', '331735646338', '683-102-776-000', '663904995411'),
(4, 10003, '30-8870406-2', '177451189665', '971-711-280-000', '171519773969'),
(5, 10004, '40-2511815-0', '341911411254', '876-809-437-000', '416946776041'),
(6, 10005, '50-5577638-1', '957436191812', '031-702-374-000', '952347222457'),
(7, 10006, '49-1632020-8', '382189453145', '317-674-022-000', '441093369646'),
(8, 10007, '40-2400714-1', '239192926939', '672-474-690-000', '210850209964'),
(9, 10008, '55-4476527-2', '545652640232', '888-572-294-000', '211385556888'),
(10, 10009, '41-0644692-3', '708988234853', '604-997-793-000', '260107732354'),
(11, 10010, '64-7605054-4', '578114853194', '525-420-419-000', '799254095212'),
(12, 10011, '26-9647608-3', '126445315651', '210-805-911-000', '218002473454'),
(13, 10012, '44-8563448-3', '431709011012', '218-489-737-000', '113071293354'),
(14, 10013, '45-5656375-0', '233693897247', '210-835-851-000', '631130283546'),
(15, 10014, '27-2090996-4', '515741057496', '275-792-513-000', '101205445886'),
(16, 10015, '26-8768374-1', '308366860059', '598-065-761-000', '223057707853'),
(17, 10016, '49-2959312-6', '824187961962', '103-100-522-000', '631052853464'),
(18, 10017, '27-2090208-8', '587272469938', '482-259-498-000', '719007608464'),
(19, 10018, '45-3251383-0', '745148459521', '121-203-336-000', '114901859343'),
(20, 10019, '49-1629900-2', '579253435499', '122-244-511-000', '265104358643'),
(21, 10020, '49-1647342-5', '399665157135', '273-970-941-000', '260054585575'),
(22, 10021, '45-5617168-2', '606386917510', '354-650-951-000', '104907708845'),
(23, 10022, '52-0109570-6', '357451271274', '187-500-345-000', '113017988667'),
(24, 10023, '52-9883524-3', '548670482885', '101-558-994-000', '360028104576'),
(25, 10024, '45-5866331-6', '953901539995', '560-735-732-000', '913108649964'),
(26, 10025, '47-1692793-0', '753800654114', '841-177-857-000', '210546661243'),
(27, 10026, '40-9504657-8', '797639382265', '502-995-671-000', '210897095686'),
(28, 10027, '45-3298166-4', '810909286264', '336-676-445-000', '211274476563'),
(29, 10028, '40-2400719-4', '934389652994', '210-395-397-000', '122238077997'),
(30, 10029, '60-1152206-4', '351830469744', '395-032-717-000', '212141893454'),
(31, 10030, '54-1331005-0', '465087894112', '215-973-013-000', '515012579765'),
(32, 10031, '52-1859253-1', '136451303068', '599-312-588-000', '110018813465'),
(33, 10032, '26-7145133-4', '601644902402', '404-768-309-000', '697764069311'),
(34, 10033, '11-5062972-7', '380685387212', '256-436-296-000', '993372963726'),
(35, 10034, '20-2987501-5', '918460050077', '911-529-713-000', '874042259378');

-- --------------------------------------------------------

--
-- Table structure for table `leave_balances`
--

CREATE TABLE `leave_balances` (
  `leave_balance_id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `leave_type_id` int(11) NOT NULL,
  `remaining_balance` decimal(5,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `leave_balances`
--

INSERT INTO `leave_balances` (`leave_balance_id`, `employee_id`, `leave_type_id`, `remaining_balance`) VALUES
(1, 10000, 1, 15.00),
(2, 10000, 2, 15.00),
(3, 10001, 1, 15.00),
(4, 10001, 2, 15.00),
(5, 10002, 1, 15.00),
(6, 10002, 2, 15.00),
(7, 10003, 1, 15.00),
(8, 10003, 2, 15.00),
(9, 10004, 1, 15.00),
(10, 10004, 2, 15.00),
(11, 10005, 1, 15.00),
(12, 10005, 2, 15.00),
(13, 10006, 1, 15.00),
(14, 10006, 2, 15.00),
(15, 10007, 1, 15.00),
(16, 10007, 2, 15.00),
(17, 10008, 1, 15.00),
(18, 10008, 2, 15.00),
(19, 10009, 1, 15.00),
(20, 10009, 2, 15.00),
(21, 10010, 1, 15.00),
(22, 10010, 2, 15.00),
(23, 10011, 1, 15.00),
(24, 10011, 2, 15.00),
(25, 10012, 1, 15.00),
(26, 10012, 2, 15.00),
(27, 10013, 1, 15.00),
(28, 10013, 2, 15.00),
(29, 10014, 1, 15.00),
(30, 10014, 2, 15.00),
(31, 10015, 1, 15.00),
(32, 10015, 2, 15.00),
(33, 10016, 1, 15.00),
(34, 10016, 2, 15.00),
(35, 10017, 1, 15.00),
(36, 10017, 2, 15.00),
(37, 10018, 1, 15.00),
(38, 10018, 2, 15.00),
(39, 10019, 1, 15.00),
(40, 10019, 2, 15.00),
(41, 10020, 1, 15.00),
(42, 10020, 2, 15.00),
(43, 10021, 1, 15.00),
(44, 10021, 2, 15.00),
(45, 10022, 1, 15.00),
(46, 10022, 2, 15.00),
(47, 10023, 1, 15.00),
(48, 10023, 2, 15.00),
(49, 10024, 1, 15.00),
(50, 10024, 2, 15.00),
(51, 10025, 1, 15.00),
(52, 10025, 2, 15.00),
(53, 10026, 1, 15.00),
(54, 10026, 2, 15.00),
(55, 10027, 1, 15.00),
(56, 10027, 2, 15.00),
(57, 10028, 1, 15.00),
(58, 10028, 2, 15.00),
(59, 10029, 1, 15.00),
(60, 10029, 2, 15.00),
(61, 10030, 1, 15.00),
(62, 10030, 2, 15.00),
(63, 10031, 1, 15.00),
(64, 10031, 2, 15.00),
(65, 10032, 1, 15.00),
(66, 10032, 2, 15.00),
(67, 10033, 1, 15.00),
(68, 10033, 2, 15.00),
(69, 10034, 1, 15.00),
(70, 10034, 2, 15.00);

-- --------------------------------------------------------

--
-- Table structure for table `leave_requests`
--

CREATE TABLE `leave_requests` (
  `leave_request_id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `leave_type_id` int(11) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` enum('Pending','Approved','Rejected') NOT NULL DEFAULT 'Pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `leave_requests`
--

INSERT INTO `leave_requests` (`leave_request_id`, `employee_id`, `leave_type_id`, `start_date`, `end_date`, `status`) VALUES
(1, 10001, 1, '2025-01-20', '2025-01-22', 'Approved'),
(2, 10008, 2, '2025-01-15', '2025-01-15', 'Approved'),
(3, 10009, 1, '2025-02-03', '2025-02-05', 'Pending'),
(4, 10015, 2, '2025-01-28', '2025-01-29', 'Approved'),
(5, 10021, 1, '2025-02-10', '2025-02-12', 'Pending');

-- --------------------------------------------------------

--
-- Table structure for table `leave_types`
--

CREATE TABLE `leave_types` (
  `leave_type_id` int(11) NOT NULL,
  `leave_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `leave_types`
--

INSERT INTO `leave_types` (`leave_type_id`, `leave_name`) VALUES
(2, 'Sick Leave'),
(1, 'Vacation Leave');

-- --------------------------------------------------------

--
-- Table structure for table `overtime_logs`
--

CREATE TABLE `overtime_logs` (
  `overtime_id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `payroll_id` int(11) NOT NULL,
  `ot_date` date NOT NULL,
  `hours_worked` decimal(5,2) NOT NULL,
  `overtime_type` enum('Regular','Rest Day','Night Differential') NOT NULL DEFAULT 'Regular',
  `status` enum('Pending','Approved','Rejected') NOT NULL DEFAULT 'Pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `pagibig_contributions`
--

CREATE TABLE `pagibig_contributions` (
  `id` int(11) NOT NULL,
  `salary_min` decimal(12,2) NOT NULL,
  `salary_max` decimal(12,2) DEFAULT NULL,
  `employee_rate` decimal(5,4) NOT NULL,
  `employer_rate` decimal(5,4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `pagibig_contributions`
--

INSERT INTO `pagibig_contributions` (`id`, `salary_min`, `salary_max`, `employee_rate`, `employer_rate`) VALUES
(1, 1000.00, 1500.00, 0.0100, 0.0200),
(2, 1500.01, NULL, 0.0200, 0.0200);

-- --------------------------------------------------------

--
-- Table structure for table `payroll`
--

CREATE TABLE `payroll` (
  `payroll_id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `payroll_period` varchar(50) NOT NULL,
  `regular_hours_worked` decimal(6,2) NOT NULL DEFAULT 0.00,
  `overtime_hours_worked` decimal(6,2) NOT NULL DEFAULT 0.00,
  `gross_pay` decimal(12,2) NOT NULL DEFAULT 0.00,
  `total_deductions` decimal(12,2) NOT NULL DEFAULT 0.00,
  `net_pay` decimal(12,2) NOT NULL DEFAULT 0.00,
  `date_processed` timestamp NOT NULL DEFAULT current_timestamp(),
  `remarks` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `payroll`
--

INSERT INTO `payroll` (`payroll_id`, `employee_id`, `payroll_period`, `regular_hours_worked`, `overtime_hours_worked`, `gross_pay`, `total_deductions`, `net_pay`, `date_processed`, `remarks`) VALUES
(8, 10000, '2026-02-16 to 2026-02-28', 80.00, 0.00, 45000.00, 8775.00, 36225.00, '2026-06-18 05:21:34', NULL),
(13, 10009, '2026-07-01 to 2026-07-15', 80.00, 0.00, 11250.00, 2193.75, 9056.25, '2026-06-18 05:21:34', NULL),
(14, 10000, '2026-01-01 to 2026-01-15', 80.00, 0.00, 45000.00, 8775.00, 36225.00, '2026-06-18 05:21:34', NULL),
(15, 10006, '2026-03-01 to 2026-03-15', 80.00, 0.00, 26335.00, 5135.33, 21199.68, '2026-06-18 05:21:34', NULL),
(16, 10007, '2026-01-01 to 2026-01-15', 80.00, 0.00, 21488.00, 4190.16, 17297.84, '2026-06-18 05:21:34', NULL),
(17, 10008, '2026-03-01 to 2026-03-15', 80.00, 0.00, 11250.00, 2193.75, 9056.25, '2026-06-18 05:21:34', NULL),
(19, 10010, '2026-11-01 to 2026-11-15', 80.00, 0.00, 26335.00, 5135.33, 21199.68, '2026-06-19 03:53:00', NULL),
(20, 10001, '2026-01-01 to 2026-01-15', 80.00, 0.00, 45000.00, 8775.00, 36225.00, '2026-06-19 03:53:59', NULL),
(21, 10001, '2026-05-16 to 2026-05-31', 80.00, 0.00, 45000.00, 8775.00, 36225.00, '2026-06-19 03:54:19', NULL),
(22, 10002, '2026-03-01 to 2026-03-15', 80.00, 0.00, 30000.00, 5850.00, 24150.00, '2026-06-19 03:54:35', NULL),
(23, 10002, '2026-04-01 to 2026-04-15', 80.00, 0.00, 30000.00, 5850.00, 24150.00, '2026-06-19 03:54:41', NULL),
(24, 10003, '2026-03-16 to 2026-03-31', 80.00, 0.00, 30000.00, 5850.00, 24150.00, '2026-06-19 03:54:56', NULL),
(25, 10003, '2026-04-16 to 2026-04-30', 80.00, 0.00, 30000.00, 5850.00, 24150.00, '2026-06-19 03:55:00', NULL),
(26, 10004, '2026-01-01 to 2026-01-15', 80.00, 0.00, 30000.00, 5850.00, 24150.00, '2026-06-19 03:55:21', NULL),
(27, 10004, '2026-03-16 to 2026-03-31', 80.00, 0.00, 30000.00, 5850.00, 24150.00, '2026-06-19 03:55:24', NULL),
(28, 10005, '2026-03-16 to 2026-03-31', 80.00, 0.00, 26335.00, 5135.33, 21199.68, '2026-06-19 03:55:34', NULL),
(29, 10005, '2026-01-01 to 2026-01-15', 80.00, 0.00, 26335.00, 5135.33, 21199.68, '2026-06-19 03:55:37', NULL),
(30, 10006, '2026-01-01 to 2026-01-15', 80.00, 0.00, 26335.00, 5135.33, 21199.68, '2026-06-19 03:55:48', NULL),
(31, 10006, '2026-03-01 to 2026-03-15', 80.00, 0.00, 26335.00, 5135.33, 21199.68, '2026-06-19 03:55:51', NULL),
(32, 10007, '2026-02-01 to 2026-02-15', 80.00, 0.00, 21488.00, 4190.16, 17297.84, '2026-06-19 03:56:03', NULL),
(33, 10007, '2026-04-01 to 2026-04-15', 80.00, 0.00, 21488.00, 4190.16, 17297.84, '2026-06-19 03:56:07', NULL),
(34, 10008, '2026-04-01 to 2026-04-15', 80.00, 0.00, 11250.00, 2193.75, 9056.25, '2026-06-19 03:56:15', NULL),
(35, 10008, '2026-04-01 to 2026-04-15', 80.00, 0.00, 11250.00, 2193.75, 9056.25, '2026-06-19 03:56:19', NULL),
(36, 10008, '2026-02-01 to 2026-02-15', 80.00, 0.00, 11250.00, 2193.75, 9056.25, '2026-06-19 03:56:25', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `permissions`
--

CREATE TABLE `permissions` (
  `permission_id` int(11) NOT NULL,
  `permission_name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `permissions`
--

INSERT INTO `permissions` (`permission_id`, `permission_name`, `description`) VALUES
(1, 'VIEW_EMPLOYEE', 'View employee records'),
(2, 'CREATE_EMPLOYEE', 'Add new employees to the system'),
(3, 'EDIT_EMPLOYEE', 'Modify existing employee records'),
(4, 'DELETE_EMPLOYEE', 'Remove employees from the system'),
(5, 'VIEW_PAYROLL', 'View payroll records'),
(6, 'CREATE_PAYROLL', 'Generate payroll for a period'),
(7, 'EDIT_PAYROLL', 'Modify existing payroll records'),
(8, 'VIEW_ATTENDANCE', 'View attendance logs'),
(9, 'EDIT_ATTENDANCE', 'Modify attendance records'),
(10, 'VIEW_LEAVE', 'View leave requests'),
(11, 'MANAGE_LEAVE', 'Approve or reject leave requests'),
(12, 'VIEW_OVERTIME', 'View overtime logs'),
(13, 'MANAGE_OVERTIME', 'Approve or reject overtime requests'),
(14, 'VIEW_OWN_PAYSLIP', 'View own payslip'),
(15, 'VIEW_OWN_ATTENDANCE', 'View own attendance records'),
(16, 'SUBMIT_LEAVE_REQUEST', 'Submit a leave request'),
(17, 'SUBMIT_OVERTIME_REQUEST', 'Submit an overtime request'),
(18, 'MANAGE_ROLES', 'Assign roles and permissions'),
(19, 'VIEW_REPORTS', 'View payroll and HR reports'),
(20, 'MANAGE_SYSTEM', 'Access system configuration and settings');

-- --------------------------------------------------------

--
-- Table structure for table `philhealth_contributions`
--

CREATE TABLE `philhealth_contributions` (
  `id` int(11) NOT NULL,
  `monthly_salary_min` decimal(12,2) NOT NULL,
  `monthly_salary_max` decimal(12,2) DEFAULT NULL,
  `premium_rate` decimal(5,4) NOT NULL,
  `monthly_premium_cap` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `philhealth_contributions`
--

INSERT INTO `philhealth_contributions` (`id`, `monthly_salary_min`, `monthly_salary_max`, `premium_rate`, `monthly_premium_cap`) VALUES
(1, 0.00, 9999.99, 0.0300, 300.00),
(2, 10000.00, 59999.99, 0.0300, NULL),
(3, 60000.00, NULL, 0.0300, 1800.00);

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `role_id` int(11) NOT NULL,
  `role_name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`role_id`, `role_name`) VALUES
(4, 'Employee'),
(3, 'Finance Personnel'),
(2, 'HR Personnel'),
(5, 'IT Personnel'),
(1, 'System Administrator');

-- --------------------------------------------------------

--
-- Table structure for table `role_permissions`
--

CREATE TABLE `role_permissions` (
  `role_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `role_permissions`
--

INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(1, 8),
(1, 9),
(1, 10),
(1, 11),
(1, 12),
(1, 13),
(1, 14),
(1, 15),
(1, 16),
(1, 17),
(1, 18),
(1, 19),
(1, 20),
(2, 1),
(2, 2),
(2, 3),
(2, 8),
(2, 9),
(2, 10),
(2, 11),
(2, 12),
(2, 13),
(2, 14),
(2, 15),
(2, 19),
(3, 1),
(3, 5),
(3, 6),
(3, 7),
(3, 14),
(3, 15),
(3, 19),
(4, 14),
(4, 15),
(4, 16),
(4, 17),
(5, 1),
(5, 8),
(5, 15),
(5, 19),
(5, 20);

-- --------------------------------------------------------

--
-- Table structure for table `sss_contributions`
--

CREATE TABLE `sss_contributions` (
  `id` int(11) NOT NULL,
  `range_min` decimal(12,2) NOT NULL,
  `range_max` decimal(12,2) DEFAULT NULL,
  `contribution` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `sss_contributions`
--

INSERT INTO `sss_contributions` (`id`, `range_min`, `range_max`, `contribution`) VALUES
(1, 0.00, 3249.99, 135.00),
(2, 3250.00, 3749.99, 157.50),
(3, 3750.00, 4249.99, 180.00),
(4, 4250.00, 4749.99, 202.50),
(5, 4750.00, 5249.99, 225.00),
(6, 5250.00, 5749.99, 247.50),
(7, 5750.00, 6249.99, 270.00),
(8, 6250.00, 6749.99, 292.50),
(9, 6750.00, 7249.99, 315.00),
(10, 7250.00, 7749.99, 337.50),
(11, 7750.00, 8249.99, 360.00),
(12, 8250.00, 8749.99, 382.50),
(13, 8750.00, 9249.99, 405.00),
(14, 9250.00, 9749.99, 427.50),
(15, 9750.00, 10249.99, 450.00),
(16, 10250.00, 10749.99, 472.50),
(17, 10750.00, 11249.99, 495.00),
(18, 11250.00, 11749.99, 517.50),
(19, 11750.00, 12249.99, 540.00),
(20, 12250.00, 12749.99, 562.50),
(21, 12750.00, 13249.99, 585.00),
(22, 13250.00, 13749.99, 607.50),
(23, 13750.00, 14249.99, 630.00),
(24, 14250.00, 14749.99, 652.50),
(25, 14750.00, 15249.99, 675.00),
(26, 15250.00, 15749.99, 697.50),
(27, 15750.00, 16249.99, 720.00),
(28, 16250.00, 16749.99, 742.50),
(29, 16750.00, 17249.99, 765.00),
(30, 17250.00, 17749.99, 787.50),
(31, 17750.00, 18249.99, 810.00),
(32, 18250.00, 18749.99, 832.50),
(33, 18750.00, 19249.99, 855.00),
(34, 19250.00, 19749.99, 877.50),
(35, 19750.00, 20249.99, 900.00),
(36, 20250.00, 20749.99, 922.50),
(37, 20750.00, 21249.99, 945.00),
(38, 21250.00, 21749.99, 967.50),
(39, 21750.00, 22249.99, 990.00),
(40, 22250.00, 22749.99, 1012.50),
(41, 22750.00, 23249.99, 1035.00),
(42, 23250.00, 23749.99, 1057.50),
(43, 23750.00, 24249.99, 1080.00),
(44, 24250.00, 24749.99, 1102.50),
(45, 24750.00, NULL, 1125.00);

-- --------------------------------------------------------

--
-- Table structure for table `withholding_tax`
--

CREATE TABLE `withholding_tax` (
  `id` int(11) NOT NULL,
  `taxable_min` decimal(12,2) NOT NULL,
  `taxable_max` decimal(12,2) DEFAULT NULL,
  `fixed_tax` decimal(12,2) NOT NULL DEFAULT 0.00,
  `excess_rate` decimal(5,4) NOT NULL DEFAULT 0.0000,
  `excess_over` decimal(12,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `withholding_tax`
--

INSERT INTO `withholding_tax` (`id`, `taxable_min`, `taxable_max`, `fixed_tax`, `excess_rate`, `excess_over`) VALUES
(1, 0.00, 20832.00, 0.00, 0.0000, 0.00),
(2, 20832.01, 33332.99, 0.00, 0.2000, 20833.00),
(3, 33333.00, 66666.99, 2500.00, 0.2500, 33333.00),
(4, 66667.00, 166666.99, 10833.00, 0.3000, 66667.00),
(5, 166667.00, 666666.99, 40833.33, 0.3200, 166667.00),
(6, 666667.00, NULL, 200833.33, 0.3500, 666667.00);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
  ADD PRIMARY KEY (`attendance_id`),
  ADD UNIQUE KEY `unique_attendance` (`employee_id`,`attendance_date`),
  ADD KEY `fk_attendance_employee` (`employee_id`);

--
-- Indexes for table `benefits`
--
ALTER TABLE `benefits`
  ADD PRIMARY KEY (`benefit_id`),
  ADD UNIQUE KEY `uq_benefit_name` (`benefit_name`);

--
-- Indexes for table `compensation`
--
ALTER TABLE `compensation`
  ADD PRIMARY KEY (`compensation_id`),
  ADD UNIQUE KEY `uq_comp_employee` (`employee_id`);

--
-- Indexes for table `compensation_details`
--
ALTER TABLE `compensation_details`
  ADD PRIMARY KEY (`compensation_detail_id`),
  ADD UNIQUE KEY `uq_comp_benefit` (`compensation_id`,`benefit_id`),
  ADD KEY `fk_detail_benefit` (`benefit_id`);

--
-- Indexes for table `credentials`
--
ALTER TABLE `credentials`
  ADD PRIMARY KEY (`employee_id`),
  ADD KEY `fk_cred_role` (`role_id`);

--
-- Indexes for table `deduction`
--
ALTER TABLE `deduction`
  ADD PRIMARY KEY (`deduction_id`),
  ADD KEY `fk_deduction_payroll` (`payroll_id`),
  ADD KEY `fk_deduction_type` (`deduction_type_id`);

--
-- Indexes for table `deduction_types`
--
ALTER TABLE `deduction_types`
  ADD PRIMARY KEY (`deduction_type_id`),
  ADD UNIQUE KEY `uq_deduction_name` (`deduction_name`);

--
-- Indexes for table `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`employee_id`),
  ADD KEY `fk_emp_supervisor` (`supervisor_id`);

--
-- Indexes for table `employee_government_ids`
--
ALTER TABLE `employee_government_ids`
  ADD PRIMARY KEY (`gov_id`),
  ADD UNIQUE KEY `uq_emp_gov_id` (`employee_id`),
  ADD UNIQUE KEY `uq_sss` (`sss_number`),
  ADD UNIQUE KEY `uq_philhealth` (`philhealth_number`),
  ADD UNIQUE KEY `uq_tin` (`tin_number`),
  ADD UNIQUE KEY `uq_pagibig` (`pagibig_number`);

--
-- Indexes for table `leave_balances`
--
ALTER TABLE `leave_balances`
  ADD PRIMARY KEY (`leave_balance_id`),
  ADD UNIQUE KEY `uq_emp_leave` (`employee_id`,`leave_type_id`),
  ADD KEY `fk_lb_leave_type` (`leave_type_id`);

--
-- Indexes for table `leave_requests`
--
ALTER TABLE `leave_requests`
  ADD PRIMARY KEY (`leave_request_id`),
  ADD KEY `fk_lr_employee` (`employee_id`),
  ADD KEY `fk_lr_leave_type` (`leave_type_id`);

--
-- Indexes for table `leave_types`
--
ALTER TABLE `leave_types`
  ADD PRIMARY KEY (`leave_type_id`),
  ADD UNIQUE KEY `uq_leave_name` (`leave_name`);

--
-- Indexes for table `overtime_logs`
--
ALTER TABLE `overtime_logs`
  ADD PRIMARY KEY (`overtime_id`),
  ADD KEY `fk_ot_employee` (`employee_id`),
  ADD KEY `fk_ot_payroll` (`payroll_id`);

--
-- Indexes for table `pagibig_contributions`
--
ALTER TABLE `pagibig_contributions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `payroll`
--
ALTER TABLE `payroll`
  ADD PRIMARY KEY (`payroll_id`),
  ADD KEY `fk_payroll_employee` (`employee_id`);

--
-- Indexes for table `permissions`
--
ALTER TABLE `permissions`
  ADD PRIMARY KEY (`permission_id`),
  ADD UNIQUE KEY `uq_permission_name` (`permission_name`);

--
-- Indexes for table `philhealth_contributions`
--
ALTER TABLE `philhealth_contributions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`role_id`),
  ADD UNIQUE KEY `uq_role_name` (`role_name`);

--
-- Indexes for table `role_permissions`
--
ALTER TABLE `role_permissions`
  ADD PRIMARY KEY (`role_id`,`permission_id`),
  ADD KEY `fk_rp_permission` (`permission_id`);

--
-- Indexes for table `sss_contributions`
--
ALTER TABLE `sss_contributions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `withholding_tax`
--
ALTER TABLE `withholding_tax`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
  MODIFY `attendance_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=315;

--
-- AUTO_INCREMENT for table `benefits`
--
ALTER TABLE `benefits`
  MODIFY `benefit_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `compensation`
--
ALTER TABLE `compensation`
  MODIFY `compensation_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT for table `compensation_details`
--
ALTER TABLE `compensation_details`
  MODIFY `compensation_detail_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=115;

--
-- AUTO_INCREMENT for table `deduction`
--
ALTER TABLE `deduction`
  MODIFY `deduction_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=705;

--
-- AUTO_INCREMENT for table `deduction_types`
--
ALTER TABLE `deduction_types`
  MODIFY `deduction_type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `employee_government_ids`
--
ALTER TABLE `employee_government_ids`
  MODIFY `gov_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- AUTO_INCREMENT for table `leave_balances`
--
ALTER TABLE `leave_balances`
  MODIFY `leave_balance_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=71;

--
-- AUTO_INCREMENT for table `leave_requests`
--
ALTER TABLE `leave_requests`
  MODIFY `leave_request_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `leave_types`
--
ALTER TABLE `leave_types`
  MODIFY `leave_type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `overtime_logs`
--
ALTER TABLE `overtime_logs`
  MODIFY `overtime_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `pagibig_contributions`
--
ALTER TABLE `pagibig_contributions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `payroll`
--
ALTER TABLE `payroll`
  MODIFY `payroll_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=172;

--
-- AUTO_INCREMENT for table `permissions`
--
ALTER TABLE `permissions`
  MODIFY `permission_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `philhealth_contributions`
--
ALTER TABLE `philhealth_contributions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `role_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `sss_contributions`
--
ALTER TABLE `sss_contributions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=46;

--
-- AUTO_INCREMENT for table `withholding_tax`
--
ALTER TABLE `withholding_tax`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `attendance`
--
ALTER TABLE `attendance`
  ADD CONSTRAINT `fk_attendance_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `compensation`
--
ALTER TABLE `compensation`
  ADD CONSTRAINT `fk_comp_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `compensation_details`
--
ALTER TABLE `compensation_details`
  ADD CONSTRAINT `fk_detail_benefit` FOREIGN KEY (`benefit_id`) REFERENCES `benefits` (`benefit_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_detail_compensation` FOREIGN KEY (`compensation_id`) REFERENCES `compensation` (`compensation_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `credentials`
--
ALTER TABLE `credentials`
  ADD CONSTRAINT `fk_cred_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cred_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON UPDATE CASCADE;

--
-- Constraints for table `deduction`
--
ALTER TABLE `deduction`
  ADD CONSTRAINT `fk_deduction_payroll` FOREIGN KEY (`payroll_id`) REFERENCES `payroll` (`payroll_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_deduction_type` FOREIGN KEY (`deduction_type_id`) REFERENCES `deduction_types` (`deduction_type_id`) ON UPDATE CASCADE;

--
-- Constraints for table `employees`
--
ALTER TABLE `employees`
  ADD CONSTRAINT `fk_emp_supervisor` FOREIGN KEY (`supervisor_id`) REFERENCES `employees` (`employee_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `employee_government_ids`
--
ALTER TABLE `employee_government_ids`
  ADD CONSTRAINT `fk_gov_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `leave_balances`
--
ALTER TABLE `leave_balances`
  ADD CONSTRAINT `fk_lb_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lb_leave_type` FOREIGN KEY (`leave_type_id`) REFERENCES `leave_types` (`leave_type_id`) ON UPDATE CASCADE;

--
-- Constraints for table `leave_requests`
--
ALTER TABLE `leave_requests`
  ADD CONSTRAINT `fk_lr_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lr_leave_type` FOREIGN KEY (`leave_type_id`) REFERENCES `leave_types` (`leave_type_id`) ON UPDATE CASCADE;

--
-- Constraints for table `overtime_logs`
--
ALTER TABLE `overtime_logs`
  ADD CONSTRAINT `fk_ot_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ot_payroll` FOREIGN KEY (`payroll_id`) REFERENCES `payroll` (`payroll_id`) ON UPDATE CASCADE;

--
-- Constraints for table `payroll`
--
ALTER TABLE `payroll`
  ADD CONSTRAINT `fk_payroll_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON UPDATE CASCADE;

--
-- Constraints for table `role_permissions`
--
ALTER TABLE `role_permissions`
  ADD CONSTRAINT `fk_rp_permission` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
