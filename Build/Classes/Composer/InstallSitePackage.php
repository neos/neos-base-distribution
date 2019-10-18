<?php
namespace Neos\BaseDistribution\Composer;

use Composer\Console\Application;
use Composer\DependencyResolver\Operation\InstallOperation;
use Composer\DependencyResolver\Operation\UpdateOperation;
use Composer\Composer;
use Composer\Factory;
use Composer\Json\JsonFile;
use Composer\Package\Version\VersionParser;
use Composer\Repository\CompositeRepository;
use Composer\Repository\PlatformRepository;
use Composer\Script\Event;
use Composer\Installer\PackageEvent;
use Composer\Util\Silencer;
use Symfony\Component\Console\Input\ArrayInput;
use Symfony\Component\Console\Output\ConsoleOutput;
use Symfony\Component\Yaml\Yaml;
use Neos\Utility\Files;
use Neos\Utility\Arrays;
use Neos\Splash\DistributionBuilder\Service\PackageService;
use Neos\Splash\DistributionBuilder\Service\JsonFileService;
use Neos\Splash\DistributionBuilder\Domain\ValueObjects\PackageRequirement;

/**
 *
 */
class InstallSitePackage
{
    const LOCAL_SRC_PATH = 'DistributionPackages';

    /**
     * Setup the neos distribution
     *
     * @param Event $event
     * @throws \Neos\Utility\Exception\FilesException
     */
    public static function setupDistribution(Event $event)
    {
        if (!defined('FLOW_PATH_ROOT')) {
            define('FLOW_PATH_ROOT', Files::getUnixStylePath(getcwd()) . '/');
        }
        $composer = $event->getComposer();
        $io = $event->getIO();

        $distributionReadyMessagesBase = [
            '',
            'Your Neos was prepared successfully.',
            '',
            'For local development you still have to:',
            '1. Add database credentials to Configuration/Development/Settings.yaml (or start the setup by calling the /setup url)',
            '2. Migrate database "./flow doctrine:migrate"',
        ];

        $io->write([
            '',
            'Welcome to Neos',
            ''
        ]);

        if (!$io->isInteractive()) {
            $io->write('Non-Interacctive installation, installing no additional package(s).');
            $io->write($distributionReadyMessagesBase);
            return;
        }

        $choices = [
            'start with the Neos Demo content',
            'empty Neos'
        ];

        $packages = [
          'neos/demo',
          ''
        ];

        $selection = $io->select('How would you like your Neos configured?', $choices, 1);
        if ((int)$selection === 1) {
            $io->write('No package will be installed.');
            $io->write($distributionReadyMessagesBase);
            $io->write('3. Create your site package "./flow site:create"');
            return;
        }

        $output = new ConsoleOutput();
        $composerApplication = new Application();
        $composerApplication->doRun(new ArrayInput([
            'command' => 'require',
            'packages' => [$packages[(int)$selection]]
        ]), $output);

        // success
        $io->write($distributionReadyMessagesBase);
        $io->write('3. Import site data "./flow site:import --package-key <Package.Name>" (where Package.Name could be Neos.Demo for example)');
    }
}
