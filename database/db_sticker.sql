-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 31, 2021 at 11:39 AM
-- Server version: 10.1.31-MariaDB
-- PHP Version: 7.3.23

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_sticker`
--

-- --------------------------------------------------------

--
-- Table structure for table `tb_packs`
--

CREATE TABLE `tb_packs` (
  `identifier` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `author` varchar(100) NOT NULL,
  `trayImageFileName` varchar(100) NOT NULL,
  `trayImageFile` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tb_packs`
--

INSERT INTO `tb_packs` (`identifier`, `name`, `author`, `trayImageFileName`, `trayImageFile`) VALUES
('fbp5nqxv1za4kixvo5b0', 'test', 'ttest 1', '05d7fk2s0lreclk5ehjo.png', ''),
('peitmvafm02kir2emnyv', 'zzz', 'zzz', 'vyp172dlnrxewo2nxidn.png', ''),
('w0jh1tinbwltvao7wv01', 'ssss', 'sssss', '20iu57e8jxuyq51rtl2n.png', '');

-- --------------------------------------------------------

--
-- Table structure for table `tb_stickers`
--

CREATE TABLE `tb_stickers` (
  `id` int(11) NOT NULL,
  `image_file_name` varchar(100) NOT NULL,
  `image_file` longtext NOT NULL,
  `size` int(11) NOT NULL,
  `identifier` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tb_stickers`
--

INSERT INTO `tb_stickers` (`id`, `image_file_name`, `image_file`, `size`, `identifier`) VALUES
(44, 'k0eo5mz2bqjl032u0khq.webp', '', 0, 'fbp5nqxv1za4kixvo5b0'),
(45, '6qknlwlsk4o4ppcy5gk4.webp', '', 0, 'fbp5nqxv1za4kixvo5b0'),
(46, 'fgshyip5hy7jw06ttlvt.webp', '', 0, 'fbp5nqxv1za4kixvo5b0'),
(47, 'hqcsq1aifqb63kewg3w0.webp', '', 0, 'fbp5nqxv1za4kixvo5b0'),
(48, 'n5i7mdm4jejeia8dljog.webp', '', 0, 'peitmvafm02kir2emnyv'),
(49, 'pyy8mqpsj50x537slvhd.webp', '', 0, 'peitmvafm02kir2emnyv'),
(50, 'kdnt4z8vi7wbu0c5scqe.webp', '', 0, 'peitmvafm02kir2emnyv'),
(51, 'j0y06nzwlv477wrsuw1n.webp', '', 0, 'peitmvafm02kir2emnyv'),
(52, 'g58rbfyj8mxmguykc7mb.webp', '', 0, 'peitmvafm02kir2emnyv'),
(53, 'b7zeunwimfnavx3f03yo.webp', '', 0, 'w0jh1tinbwltvao7wv01'),
(54, 'du0anodjeqvvegac53qy.webp', '', 0, 'w0jh1tinbwltvao7wv01'),
(55, 's5y5kly248ikukkdkski.webp', '', 0, 'w0jh1tinbwltvao7wv01'),
(56, 'f5dr3noof68wylx4m6cf.webp', '', 0, 'w0jh1tinbwltvao7wv01');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tb_packs`
--
ALTER TABLE `tb_packs`
  ADD PRIMARY KEY (`identifier`);

--
-- Indexes for table `tb_stickers`
--
ALTER TABLE `tb_stickers`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tb_stickers`
--
ALTER TABLE `tb_stickers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=57;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
